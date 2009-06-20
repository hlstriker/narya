//
// $Id$
//
// Narya library - tools for developing networked games
// Copyright (C) 2002-2009 Three Rings Design, Inc., All Rights Reserved
// http://www.threerings.net/code/narya/
//
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.threerings.presents.net;

/**
 * Used to communicate to the client that we received their unsubscribe
 * request and that it is now OK to remove an object mapping from their
 * local object table.
 */
public class UnsubscribeResponse extends DownstreamMessage
{
    /**
     * Zero argument constructor used when unserializing an instance.
     */
    public UnsubscribeResponse ()
    {
        super();
    }

    /**
     * Constructs an unsubscribe response with the supplied oid.
     */
    public UnsubscribeResponse (int oid)
    {
        _oid = oid;
    }

    public int getOid ()
    {
        return _oid;
    }

    @Override
    public String toString ()
    {
        return "[type=UNACK, msgid=" + messageId + ", oid=" + _oid + "]";
    }

    protected int _oid;
}
