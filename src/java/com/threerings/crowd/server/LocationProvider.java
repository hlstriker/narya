//
// $Id: LocationProvider.java,v 1.10 2001/10/11 04:07:51 mdb Exp $

package com.threerings.crowd.server;

import com.threerings.presents.dobj.DObject;
import com.threerings.presents.server.InvocationProvider;
import com.threerings.presents.server.ServiceFailedException;

import com.threerings.crowd.Log;
import com.threerings.crowd.client.LocationCodes;
import com.threerings.crowd.data.*;
import com.threerings.crowd.server.CrowdServer;

/**
 * This class provides the server end of the location services.
 */
public class LocationProvider
    extends InvocationProvider implements LocationCodes
{
    /**
     * Processes a request from a client to move to a new place.
     */
    public void handleMoveToRequest (BodyObject source, int invid,
                                     int placeId)
    {
        try {
            // do the move
            PlaceConfig config = moveTo(source, placeId);
            // and send the response
            sendResponse(source, invid, MOVE_SUCCEEDED_RESPONSE, config);

        } catch (ServiceFailedException sfe) {
            sendResponse(source, invid, MOVE_FAILED_RESPONSE,
                         sfe.getMessage());
        }
    }

    /**
     * Moves the specified body from whatever location they currently
     * occupy to the location identified by the supplied place id.
     *
     * @return the config object for the new location.
     *
     * @exception ServiceFaildException thrown if the move was not
     * successful for some reason (which will be communicated as an error
     * code in the exception's message data).
     */
    public static PlaceConfig moveTo (BodyObject source, int placeId)
        throws ServiceFailedException
    {
        int bodoid = source.getOid();

        // make sure the place in question actually exists
        PlaceManager pmgr = CrowdServer.plreg.getPlaceManager(placeId);
        if (pmgr == null) {
            Log.info("Requested to move to non-existent place " +
                     "[source=" + source + ", place=" + placeId + "].");
            throw new ServiceFailedException(NO_SUCH_PLACE);
        }

        // acquire a lock on the body object to ensure that rapid fire
        // moveto requests don't break things
        if (!source.acquireLock("moveToLock")) {
            // if we're still locked, a previous moveTo request hasn't
            // been fully processed
            throw new ServiceFailedException(MOVE_IN_PROGRESS);
        }

        // make sure they're not already in the location they're asking to
        // move to
        if (source.location == placeId) {
            throw new ServiceFailedException(ALREADY_THERE);
        }

        // find out if they were previously in some other location
        if (source.location != -1) {
            // remove them from the occupant list of the previous location
            try {
                PlaceObject pold = (PlaceObject)
                    CrowdServer.omgr.getObject(source.location);
                if (pold != null) {
                    Object key = new Integer(bodoid);
                    // remove their occupant info (which is keyed on oid)
                    pold.removeFromOccupantInfo(key);
                    // and remove them from the occupant list
                    pold.removeFromOccupants(bodoid);

                } else {
                    Log.info("Body's prior location no longer around? " +
                             "[boid=" + bodoid +
                             ", poid=" + source.location + "].");
                }

            } catch (ClassCastException cce) {
                Log.warning("Body claims to be at location which " +
                            "references non-PlaceObject!? " +
                            "[boid=" + bodoid +
                            ", poid=" + source.location  + "].");
            }
        }

        // set the body's new location
        PlaceObject place = pmgr.getPlaceObject();
        source.setLocation(place.getOid());

        // generate a new occupant info record and add it to the target
        // location
        OccupantInfo info = pmgr.buildOccupantInfo(source);
        if (info != null) {
            place.addToOccupantInfo(info);
        }

        // add the body object id to the place object's occupant list
        place.addToOccupants(bodoid);

        // and finally queue up a lock release event to release the lock
        // once all these events are processed
        source.releaseLock("moveToLock");

        return pmgr.getConfig();
    }
}
