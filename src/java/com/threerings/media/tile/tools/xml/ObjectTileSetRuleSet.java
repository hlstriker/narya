//
// $Id: ObjectTileSetRuleSet.java,v 1.1 2001/11/29 21:58:15 mdb Exp $

package com.threerings.media.tools.tile.xml;

import java.awt.Dimension;
import java.awt.Point;

import org.xml.sax.Attributes;
import org.apache.commons.digester.Digester;

import com.samskivert.util.StringUtil;
import com.samskivert.xml.CallMethodSpecialRule;

import com.threerings.media.tile.TileSet;
import com.threerings.media.tile.ObjectTileSet;

/**
 * Parses {@link ObjectTileSet} instances from a tileset description. An
 * object tileset description looks like so:
 *
 * <pre>
 * &lt;tileset name="Sample Object Tileset"&gt;
 *   &lt;imagePath&gt;path/to/image.png&lt;/imagePath&gt;
 *   &lt;!-- the widths (per row) of each tile in pixels --&gt;
 *   &lt;widths&gt;265&lt;/widths&gt;
 *   &lt;!-- the heights (per row) of each tile in pixels --&gt;
 *   &lt;heights&gt;224&lt;/heights&gt;
 *   &lt;!-- the number of tiles in each row --&gt;
 *   &lt;tileCounts&gt;4&lt;/tileCounts&gt;
 *   &lt;!-- the offset in pixels to the upper left tile --&gt;
 *   &lt;offsetPos&gt;0, 0&lt;/offsetPos&gt;
 *   &lt;!-- the gap between tiles in pixels --&gt;
 *   &lt;gapSize&gt;0, 0&lt;/gapSize&gt;
 *   &lt;!-- the widths (in unit tile count) of the objects --&gt;
 *   &lt;objectWidths&gt;4, 3, 4, 3&lt;/objectWidths&gt;
 *   &lt;!-- the heights (in unit tile count) of the objects --&gt;
 *   &lt;objectHeights&gt;3, 4, 3, 4&lt;/objectHeights&gt;
 * &lt;/tileset&gt;
 * </pre>
 */
public class ObjectTileSetRuleSet extends TileSetRuleSet
{
    // documentation inherited
    public void addRuleInstances (Digester digester)
    {
        super.addRuleInstances(digester);

        digester.addRule(
            _prefix + TILESET_PATH + "/objectWidths",
            new CallMethodSpecialRule(digester) {
                public void parseAndSet (String bodyText, Object target)
                {
                    int[] widths = StringUtil.parseIntArray(bodyText);
                    ((ObjectTileSet)target).setObjectWidths(widths);
                }
            });

        digester.addRule(
            _prefix + TILESET_PATH + "/objectHeights",
            new CallMethodSpecialRule(digester) {
                public void parseAndSet (String bodyText, Object target)
                {
                    int[] heights = StringUtil.parseIntArray(bodyText);
                    ((ObjectTileSet)target).setObjectHeights(heights);
                }
            });
    }

    // documentation inherited
    protected Class getTileSetClass ()
    {
        return ObjectTileSet.class;
    }
}
