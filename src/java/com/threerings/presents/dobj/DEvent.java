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

package com.threerings.presents.dobj;

import com.threerings.io.Streamable;

import com.threerings.presents.net.Transport;

/**
 * A distributed object event is dispatched whenever any modification is made to a distributed
 * object. It can also be dispatched purely for notification purposes, without making any
 * modifications to the object that defines the delivery group (the object's subscribers).
 */
public abstract class DEvent implements Streamable
{
    /** This event's "number". Every event dispatched by the server is numbered in monotonically
     * increasing fashion when the event is posted to the event dispatch queue. */
    public transient long eventId;

    /**
     * A zero argument constructor for unserialization from yon network.
     */
    public DEvent ()
    {
    }

    /**
     * Constructs a new distributed object event that pertains to the specified distributed object.
     */
    public DEvent (int targetOid)
    {
        this(targetOid, Transport.DEFAULT);
    }

    /**
     * Constructs a new distributed object event that pertains to the specified distributed object.
     *
     * @param transport a hint as to the type of transport desired for the event.
     */
    public DEvent (int targetOid, Transport transport)
    {
        _toid = targetOid;
        _transport = transport;
    }

    /**
     * Returns the oid of the object that is the target of this event.
     */
    public int getTargetOid ()
    {
        return _toid;
    }

    /**
     * Some events are used only internally on the server and need not be broadcast to subscribers,
     * proxy or otherwise. Such events can return true here and short-circuit the normal proxy
     * event dispatch mechanism.
     */
    public boolean isPrivate ()
    {
        return false;
    }

    /**
     * If this event applies itself immediately to the distributed object on the server and then
     * NOOPs later when {@link #applyToObject} is called, it should return true from this method.
     * If it will modify the object during its {@link #applyToObject} call, it should return false.
     */
    public boolean alreadyApplied ()
    {
        return false;
    }

    /**
     * Applies the attribute modifications represented by this event to the specified target
     * object. This is called by the distributed object manager in the course of dispatching events
     * and should not be called directly.
     *
     * @exception ObjectAccessException thrown if there is any problem applying the event to the
     * object (invalid attribute, etc.).
     *
     * @return true if the object manager should go on to notify the object's listeners of this
     * event, false if the event should be treated silently and the listeners should not be
     * notified.
     */
    public abstract boolean applyToObject (DObject target)
        throws ObjectAccessException;

    /**
     * Returns the object id of the client that generated this event. If the event was generated by
     * the server, the value returned will be -1. This is not valid on the client, it will return
     * -1 for all events there (it is primarily provided to allow for event-level access control).
     */
    public int getSourceOid ()
    {
        return _soid;
    }

    /**
     * Do not call this method. Sets the oid of the object on which this event operates. It is only
     * used when rewriting events during object proxying.
     */
    public void setTargetOid (int targetOid)
    {
        _toid = targetOid;
    }

    /**
     * Do not call this method. Sets the source oid of the client that generated this event. It is
     * automatically called by the client management code when a client forwards an event to the
     * server.
     */
    public void setSourceOid (int sourceOid)
    {
        _soid = sourceOid;
    }

    /**
     * Sets the transport parameters.  For events received over the network, these indicate the
     * mode of transport over which the event was received.  When an event is sent over the
     * network, these act as a hint as to the type of transport desired.
     */
    public void setTransport (Transport transport)
    {
        _transport = transport;
    }

    /**
     * Returns the transport parameters.
     */
    public Transport getTransport ()
    {
        return _transport;
    }

    /**
     * Notes the actual transport with which the event was transmitted.
     */
    public void noteActualTransport (Transport transport)
    {
        _actualTransport = transport;
    }

    /**
     * Returns the actual transport with which the event was transmitted, or <code>null</code> if
     * not yet known.
     */
    public Transport getActualTransport ()
    {
        return _actualTransport;
    }

    /**
     * Events with associated listener interfaces should implement this function and notify the
     * supplied listener if it implements their event listening interface. For example, the {@link
     * AttributeChangedEvent} will notify listeners that implement {@link AttributeChangeListener}.
     */
    protected void notifyListener (Object listener)
    {
        // the default is to do nothing
    }

    @Override
    public String toString ()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("[");
        toString(buf);
        buf.append("]");
        return buf.toString();
    }

    /**
     * This should be overridden by derived classes (which should be sure to call
     * <code>super.toString()</code>) to append the derived class specific event information to the
     * string buffer.
     */
    protected void toString (StringBuilder buf)
    {
        buf.append("targetOid=").append(_toid);
        buf.append(", sourceOid=").append(_soid);
        if (_transport != Transport.DEFAULT) {
            buf.append(", transport=").append(_transport);
        }
    }

    /** The oid of the object that is the target of this event. */
    protected int _toid;

    /** The oid of the client that generated this event. */
    protected transient int _soid = -1;

    /** The transport parameters. */
    protected transient Transport _transport = Transport.DEFAULT;

    /** The actual transport with which the event was transmitted (null if as yet unknown). */
    protected transient Transport _actualTransport;

    /** Used to differentiate between null meaning we haven't initialized our old value and null
     * being the actual old value. */
    protected static final Object UNSET_OLD_VALUE = new Object();

    /** Used to differentiate between null meaning we haven't initialized our old entry and null
     * being the actual old entry. */
    protected static final DSet.Entry UNSET_OLD_ENTRY = new DSet.Entry() {
        public Comparable<?> getKey () {
            return null;
        }
    };
}
