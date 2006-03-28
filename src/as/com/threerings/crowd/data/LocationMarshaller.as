//
// $Id: LocationMarshaller.java 3793 2005-12-21 02:12:57Z ray $
//
// Narya library - tools for developing networked games
// Copyright (C) 2002-2005 Three Rings Design, Inc., All Rights Reserved
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

package com.threerings.crowd.data {

import com.threerings.util.Integer;

import com.threerings.crowd.client.LocationService;
import com.threerings.crowd.client.MoveListener;
import com.threerings.crowd.data.PlaceConfig;
import com.threerings.presents.client.Client;
import com.threerings.presents.data.InvocationMarshaller;
import com.threerings.presents.dobj.InvocationResponseEvent;

/**
 * Provides the implementation of the {@link LocationService} interface
 * that marshalls the arguments and delivers the request to the provider
 * on the server. Also provides an implementation of the response listener
 * interfaces that marshall the response arguments and deliver them back
 * to the requesting client.
 */
public class LocationMarshaller extends InvocationMarshaller
    implements LocationService
{
    /** The method id used to dispatch {@link #leavePlace} requests. */
    public static const LEAVE_PLACE :int = 1;

    // documentation inherited from interface
    public function leavePlace (arg1 :Client) :void
    {
        sendRequest(arg1, LEAVE_PLACE, new Array());
    }

    /** The method id used to dispatch {@link #moveTo} requests. */
    public static const MOVE_TO :int = 2;

    // documentation inherited from interface
    public function moveTo (arg1 :Client, arg2 :int, arg3 :MoveListener) :void
    {
        com.threerings.crowd.Log.debug("issuing moveTo: " + _invCode);
        var listener3 :MoveMarshaller = new MoveMarshaller();
        listener3.listener = arg3;
        sendRequest(arg1, MOVE_TO, [ new Integer(arg2), listener3 ]);
    }
}
}