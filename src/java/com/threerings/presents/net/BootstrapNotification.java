//
// $Id: BootstrapNotification.java,v 1.3 2001/10/11 04:07:53 mdb Exp $

package com.threerings.presents.net;

import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import com.threerings.presents.dobj.io.DObjectFactory;

/**
 * A bootstrap notification is delivered to the client once the server has
 * fully initialized itself in preparation for dealing with this client.
 * The authentication process completes very early and further information
 * need be communicated to the client so that it can fully interact with
 * the server. This information is communicated via the bootstrap
 * notification.
 */
public class BootstrapNotification extends DownstreamMessage
{
    /** The code for a bootstrap notification. */
    public static final short TYPE = TYPE_BASE + 1;

    /**
     * Zero argument constructor used when unserializing an instance.
     */
    public BootstrapNotification ()
    {
        super();
    }

    /**
     * Constructs an bootstrap notification with the supplied data.
     */
    public BootstrapNotification (BootstrapData data)
    {
        _data = data;
    }

    public short getType ()
    {
        return TYPE;
    }

    public BootstrapData getData ()
    {
        return _data;
    }

    public void writeTo (DataOutputStream out)
        throws IOException
    {
        super.writeTo(out);
        DObjectFactory.writeTo(out, _data);
    }

    public void readFrom (DataInputStream in)
        throws IOException
    {
        super.readFrom(in);
        _data = (BootstrapData)DObjectFactory.readFrom(in);
    }

    public String toString ()
    {
        return "[type=BOOT, msgid=" + messageId + ", data=" + _data + "]";
    }

    /** The data associated with this notification. */
    protected BootstrapData _data;
}
