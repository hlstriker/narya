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

package com.threerings.admin.server.persist;

import com.samskivert.depot.Key;
import com.samskivert.depot.PersistentRecord;
import com.samskivert.depot.annotation.Column;
import com.samskivert.depot.annotation.Entity;
import com.samskivert.depot.annotation.Id;
import com.samskivert.depot.expression.ColumnExp;

/**
 * Stores information about a configuration entry in the database.
 */
@Entity(name="CONFIG")
public class ConfigRecord extends PersistentRecord
{
    // AUTO-GENERATED: FIELDS START
    public static final Class<ConfigRecord> _R = ConfigRecord.class;
    public static final ColumnExp NODE = colexp(_R, "node");
    public static final ColumnExp OBJECT = colexp(_R, "object");
    public static final ColumnExp FIELD = colexp(_R, "field");
    public static final ColumnExp VALUE = colexp(_R, "value");
    // AUTO-GENERATED: FIELDS END

    public static final int SCHEMA_VERSION = 2;

    @Id
    @Column(name="NODE", length=64)
    public String node;

    @Id
    @Column(name="OBJECT", length=128)
    public String object;

    @Id
    @Column(name="FIELD", length=64)
    public String field;

    @Column(name="VALUE", length=65535)
    public String value;

    /**
     * An empty constructor for unmarshalling.
     */
    public ConfigRecord ()
    {
        super();
    }

    public ConfigRecord (String node, String object, String field, String value)
    {
        super();
        this.node = node;
        this.object = object;
        this.field = field;
        this.value = value;
    }

    // AUTO-GENERATED: METHODS START
    /**
     * Create and return a primary {@link Key} to identify a {@link ConfigRecord}
     * with the supplied key values.
     */
    public static Key<ConfigRecord> getKey (String node, String object, String field)
    {
        return new Key<ConfigRecord>(
                ConfigRecord.class,
                new ColumnExp[] { NODE, OBJECT, FIELD },
                new Comparable[] { node, object, field });
    }
    // AUTO-GENERATED: METHODS END
}
