//
// $Id: CrowdServer.java,v 1.1 2001/07/23 21:14:27 mdb Exp $

package com.threerings.cocktail.party.server;

import java.io.IOException;

import com.threerings.cocktail.cher.server.CherServer;

import com.threerings.cocktail.party.Log;
import com.threerings.cocktail.party.data.BodyObject;

/**
 * The party server extends the cher server by configuring it to use the
 * extensions provided by the party layer to support party services.
 */
public class PartyServer extends CherServer
{
    /** The namespace used for server config properties. */
    public static final String CONFIG_KEY = "party";

    /**
     * Initializes all of the server services and prepares for operation.
     */
    public void init ()
        throws IOException
    {
        // do the cher server initialization
        super.init();

        // bind the party server config into the namespace
        config.bindProperties(CONFIG_KEY, CONFIG_PATH);

        // configure the client to use the body object
        clmgr.setClientObjectClass(BodyObject.class);

        // register our invocation service providers
        registerProviders(config.getValue(PROVIDERS_KEY, (String[])null));

        Log.info("Party server initialized.");
    }

    public static void main (String[] args)
    {
        PartyServer server = new PartyServer();
        try {
            server.init();
            server.run();
        } catch (Exception e) {
            Log.warning("Unable to initialize server.");
            Log.logStackTrace(e);
        }
    }

    // the path to the config file
    protected final static String CONFIG_PATH =
        "rsrc/config/cocktail/party/server";

    // the config key for our list of invocation provider mappings
    protected final static String PROVIDERS_KEY = CONFIG_KEY + ".providers";
}
