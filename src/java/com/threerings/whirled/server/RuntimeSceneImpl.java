//
// $Id: RuntimeSceneImpl.java,v 1.3 2003/02/04 17:25:09 mdb Exp $

package com.threerings.whirled.server;

import com.threerings.crowd.data.PlaceConfig;
import com.threerings.whirled.data.SceneModel;

/**
 * A basic implementation of the {@link RuntimeScene} interface which is
 * used by default if no extended implementation is desired.
 */
public class RuntimeSceneImpl implements RuntimeScene
{
    /**
     * Creates an instance that will obtain data from the supplied scene
     * model and place config.
     */
    public RuntimeSceneImpl (SceneModel model, PlaceConfig config)
    {
        _model = model;
        _config = config;
    }

    // documentation inherited
    public int getId ()
    {
        return _model.sceneId;
    }

    // documentation inherited
    public String getName ()
    {
        return _model.sceneName;
    }

    // documentation inherited
    public int getVersion ()
    {
        return _model.version;
    }

    // documentation inherited from interface
    public void setVersion (int version)
    {
        _model.version = version;
    }

    // documentation inherited
    public int[] getNeighborIds ()
    {
        return _model.neighborIds;
    }

    // documentation inherited
    public PlaceConfig getPlaceConfig ()
    {
        return _config;
    }

    /** A reference to our scene model. */
    protected SceneModel _model;

    /** A reference to our place configuration. */
    protected PlaceConfig _config;
}
