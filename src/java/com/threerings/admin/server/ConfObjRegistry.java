//
// $Id: ConfObjRegistry.java,v 1.5 2003/01/22 01:42:51 mdb Exp $

package com.threerings.admin.server;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import java.util.HashMap;
import java.util.Iterator;

import com.samskivert.util.Config;
import com.samskivert.util.StringUtil;

import com.threerings.presents.dobj.AccessController;
import com.threerings.presents.dobj.AttributeChangeListener;
import com.threerings.presents.dobj.AttributeChangedEvent;
import com.threerings.presents.dobj.DObject;

import com.threerings.admin.Log;

/**
 * Provides a registry of configuration distributed objects. Using
 * distributed object to store runtime configuration data can be
 * exceptionally useful in that clients (with admin privileges) can view
 * and update the running server's configuration parameters on the fly.
 *
 * <p> Users of the service are responsible for creating their own
 * configuration objects which are then registered via this class. The
 * config object registry then performs a few functions:
 *
 * <ul>
 * <li> It populates the config object with values from the persistent
 * configuration information (see {@link Config} for more information on
 * how that works).
 * <li> It mirrors object updates out to the persistent configuration
 * repository.
 * <li> It makes the set of registered objects available for inspection
 * and modification via the admin client interface.
 * </ul>
 *
 * <p> Users of this service will want to use {@link AccessController}s on
 * their configuration distributed objects to prevent non-administrators
 * from subscribing to or modifying the objects.
 */
public class ConfObjRegistry
{
    /**
     * Registers the supplied configuration object with the system.
     *
     * @param key a string that identifies this object. These are
     * generally hierarchical in nature (of the form
     * <code>system.subsystem</code>), for example:
     * <code>yohoho.crew</code>.
     * @param path The the path in the persistent configuration repository
     * (see {@link Config} for more info).
     * @param object the object to be registered.
     */
    public static void registerObject (String key, String path, DObject object)
    {
        // create a new config record for this object
        _configs.put(key, new ConfObjRecord(path, object));
    }

    /**
     * Returns the config object mapped to the specified key, or null if
     * none exists for that key.
     */
    public static DObject getObject (String key)
    {
        ConfObjRecord record = (ConfObjRecord)_configs.get(key);
        return (record == null) ? null : record.confObj;
    }

    /**
     * Returns an array containing the keys of all registered
     * configuration objects.
     */
    public static String[] getKeys ()
    {
        String[] keys = new String[_configs.size()];
        Iterator iter = _configs.keySet().iterator();
        for (int ii = 0; iter.hasNext(); ii++) {
            keys[ii] = (String)iter.next();
        }
        return keys;
    }

    /**
     * Contains all necessary info for a configuration object
     * registration.
     */
    protected static class ConfObjRecord
        implements AttributeChangeListener
    {
        public DObject confObj;
        public Config config;

        public ConfObjRecord (String path, DObject confObj)
        {
            this.config = new Config(path);
            this.confObj = confObj;

            // read in the initial configuration settings from the
            // persistent configuration repository
            Class cclass = confObj.getClass();
            try {
                Field[] fields = cclass.getFields();
                for (int ii = 0; ii < fields.length; ii++) {
                    int mods = fields[ii].getModifiers();
                    if ((mods & Modifier.STATIC) != 0 ||
                        (mods & Modifier.PUBLIC) == 0 ||
                        (mods & Modifier.TRANSIENT) != 0) {
                        continue;
                    }
                    initField(fields[ii]);
                }

                // listen for attribute updates
                confObj.addListener(this);

            } catch (SecurityException se) {
                Log.warning("Unable to reflect on " + cclass.getName() + ": " +
                            se + ". Refusing to monitor object.");
            }
        }

        public void attributeChanged (AttributeChangedEvent event)
        {
            // mirror this configuration update to the on-disk config
            String key = fieldToKey(event.getName());
            Object value = event.getValue();

            if (value instanceof Integer) {
                config.setValue(key, ((Integer)value).intValue());
            } else if (value instanceof Long) {
                config.setValue(key, ((Long)value).longValue());
            } else if (value instanceof Boolean) {
                config.setValue(key, ((Boolean)value).booleanValue());
            } else if (value instanceof String) {
                config.setValue(key, (String)value);
            } else if (value instanceof int[]) {
                config.setValue(key, (int[])value);
            } else if (value instanceof String[]) {
                config.setValue(key, (String[])value);

            } else {
                Log.info("Unable to flush config object change " +
                         "[cobj=" + confObj.getClass().getName() +
                         ", key=" + key +
                         ", type=" + value.getClass().getName() +
                         ", value=" + value + "].");
            }
        }

        /** Initializes a single field of a config distributed object from
         * its corresponding value in the associated config repository. */
        protected void initField (Field field)
        {
            String key = fieldToKey(field.getName());
            Class type = field.getType();

            try {
                if (type.equals(Boolean.TYPE)) {
                    boolean defval = field.getBoolean(confObj);
                    field.setBoolean(confObj, config.getValue(key, defval));

                } else if (type.equals(Integer.TYPE)) {
                    int defval = field.getInt(confObj);
                    field.setInt(confObj, config.getValue(key, defval));

                } else if (type.equals(Long.TYPE)) {
                    long defval = field.getLong(confObj);
                    field.setLong(confObj, config.getValue(key, defval));

                } else if (type.equals(Float.TYPE)) {
                    float defval = field.getFloat(confObj);
                    field.setFloat(confObj, config.getValue(key, defval));

                } else if (type.equals(String.class)) {
                    String defval = (String)field.get(confObj);
                    field.set(confObj, config.getValue(key, defval));

                } else if (type.equals(INT_ARRAY_PROTO.getClass())) {
                    int[] defval = (int[])field.get(confObj);
                    field.set(confObj, config.getValue(key, defval));

                } else if (type.equals(FLOAT_ARRAY_PROTO.getClass())) {
                    float[] defval = (float[])field.get(confObj);
                    field.set(confObj, config.getValue(key, defval));

                } else if (type.equals(STRING_ARRAY_PROTO.getClass())) {
                    String[] defval = (String[])field.get(confObj);
                    field.set(confObj, config.getValue(key, defval));

                } else {
                    Log.warning("Can't init field of unknown type " +
                                "[cobj=" + confObj.getClass().getName() +
                                ", key=" + key +
                                ", type=" + type.getName() + "].");
                }

            } catch (IllegalAccessException iae) {
                Log.warning("Can't set field " +
                            "[cobj=" + confObj.getClass().getName() +
                            ", key=" + key + ", error=" + iae + "].");
            }
        }

        /**
         * Converts a mixed case field name (for example,
         * <code>millisPerTick</code>) to a lower case, underscored key
         * name (in this case, <code>millis_per_tick</code>).
         */
        protected String fieldToKey (String fieldName)
        {
            StringBuffer key = new StringBuffer();
            int flength = fieldName.length();
            boolean seenLower = false;
            for (int ii = 0; ii < flength; ii++) {
                char c = fieldName.charAt(ii);
                if (Character.isUpperCase(c)) {
                    if (seenLower) {
                        key.append("_");
                    }
                    key.append(Character.toLowerCase(c));
                    seenLower = false;
                } else {
                    key.append(c);
                    seenLower = true;
                }
            }
            return key.toString();
        }

        protected static final int[] INT_ARRAY_PROTO = new int[0];
        protected static final float[] FLOAT_ARRAY_PROTO = new float[0];
        protected static final String[] STRING_ARRAY_PROTO = new String[0];
    }

    /** A mapping from identifying key to config object. */
    protected static HashMap _configs = new HashMap();
}
