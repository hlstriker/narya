//
// $Id: PlaceConfig.java,v 1.7 2004/08/23 21:05:03 mdb Exp $

package com.threerings.crowd.data;

import com.samskivert.util.StringUtil;

import com.threerings.io.TrackedStreamableObject;

/**
 * The place config class encapsulates the configuration information for a
 * particular type of place. The hierarchy of place config objects mimics
 * the hierarchy of place managers and controllers. Both the place manager
 * and place controller are provided with the place config object when the
 * place is created.
 *
 * <p> The place config object is also the mechanism used to instantiate
 * the appropriate place manager and controller. Every place must have an
 * associated place config derived class that overrides {@link
 * #getControllerClass} and {@link #getManagerClassName}, returning the
 * appropriate place controller and manager class for that place.
 */
public abstract class PlaceConfig extends TrackedStreamableObject
{
    /**
     * Returns the class that should be used to create a controller for
     * this place. The controller class must derive from {@link
     * PlaceController}.
     */
    public abstract Class getControllerClass ();

    /**
     * Returns the name of the class that should be used to create a
     * manager for this place. The manager class must derive from {@link
     * com.threerings.crowd.server.PlaceManager}. <em>Note:</em> this
     * method differs from {@link #getControllerClass} because we want to
     * avoid compile time linkage of the place config object (which is
     * used on the client) to server code. This allows a code optimizer
     * (DashO Pro, for example) to remove the server code from the client,
     * knowing that it is never used.
     */
    public abstract String getManagerClassName ();

    // documentation inherited
    protected void toString (StringBuffer buf)
    {
        buf.append("type=").append(StringUtil.shortClassName(this));
        buf.append(", ");
        super.toString(buf);
    }
}
