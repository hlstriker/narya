//
// $Id: PresentsServer.java,v 1.4 2001/06/01 22:12:03 mdb Exp $

package com.threerings.cocktail.cher.server;

import com.threerings.cocktail.cher.Log;
import com.threerings.cocktail.cher.dobj.DObjectManager;
import com.threerings.cocktail.cher.server.net.AuthManager;
import com.threerings.cocktail.cher.server.net.ConnectionManager;

/**
 * The cher server provides a central point of access to the various
 * facilities that make up the cher layer of the system.
 */
public class CherServer
{
    /** The authentication manager. */
    public static AuthManager authmgr;

    /** The manager of network connections. */
    public static ConnectionManager conmgr;

    /** The manager of clients. */
    public static ClientManager clmgr;

    /** The distributed object manager. */
    public static DObjectManager omgr;

    /**
     * Initializes all of the server services and prepares for operation.
     */
    public static void init ()
    {
        try {
            // create our authentication manager
            authmgr = new AuthManager(new DummyAuthenticator());
            // create our connection manager
            conmgr = new ConnectionManager(authmgr);
            // create our client manager
            clmgr = new ClientManager(conmgr);
            // create our distributed object manager
            omgr = new CherDObjectMgr();

        } catch (Exception e) {
            Log.warning("Unable to initialize server.");
            Log.logStackTrace(e);
        }
    }

    /**
     * Starts up all of the server services and enters the main server
     * event loop.
     */
    public static void run ()
    {
        // start up the auth manager
        authmgr.start();
        // start up the connection manager
        conmgr.start();
        // invoke the dobjmgr event loop
        ((CherDObjectMgr)omgr).run();
    }

    public static void main (String[] args)
    {
        init();
        run();
    }
}
