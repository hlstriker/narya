//
// $Id: ZoneUtil.java,v 1.3 2003/01/18 22:45:16 mdb Exp $

package com.threerings.whirled.zone.util;

/**
 * Server-specific, zone-related utility functions.
 */
public class ZoneUtil
{
    /**
     * Composes the zone type and zone id into a qualified zone id. A
     * qualified zone id is what should be passed around so that the
     * server can determine the zone type from the zone id when necessary.
     */
    public static int qualifyZoneId (byte zoneType, int zoneId)
    {
        int qualifiedZoneId = zoneType;
        qualifiedZoneId <<= 24;
        qualifiedZoneId |= zoneId;
        return qualifiedZoneId;
    }

    /**
     * Extracts the zone type from a qualified zone id.
     */
    public static int zoneType (int qualifiedZoneId)
    {
        return (0xFF000000 & qualifiedZoneId) >> 24;
    }

    /**
     * Extracts the zone id from a qualified zone id.
     */
    public static int zoneId (int qualifiedZoneId)
    {
        return (0x00FFFFFF & qualifiedZoneId);
    }

    /**
     * Returns an easier to read representation of the supplied qualified
     * zone id: <code>type:id</code>.
     */
    public static String toString (int qualifiedZoneId)
    {
        return zoneType(qualifiedZoneId) + ":" + zoneId(qualifiedZoneId);
    }
}
