//
// $Id$
//
// Narya library - tools for developing networked games
// Copyright (C) 2002-2012 Three Rings Design, Inc., All Rights Reserved
// http://code.google.com/p/narya/
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

package com.threerings.util {

import mx.resources.Locale;
import mx.resources.ResourceManager;

/**
 * The message manager provides a thin wrapper around Java's built-in
 * localization support, supporting a policy of dividing up localization
 * resources into logical units, all of the translations for which are
 * contained in a single messages file.
 *
 * <p> The message manager assumes that the locale remains constant for
 * the duration of its operation. If the locale were to change during the
 * operation of the client, a call to {@link #setLocale} should be made to
 * inform the message manager of the new locale (which will clear the
 * message bundle cache).
 */
public class MessageManager
{
    /** The name of the global resource bundle (which other bundles revert
     * to if they can't locate a message within themselves). It must be
     * named <code>global.properties</code> and live at the top of the
     * bundle hierarchy. */
    public static const GLOBAL_BUNDLE :String = "global";

    /**
     * Constructs a message manager with the supplied resource prefix and
     * the default locale. The prefix will be prepended to the path of all
     * resource bundles prior to their resolution. For example, if a
     * prefix of <code>rsrc.messages</code> was provided and a message
     * bundle with the name <code>game.chess</code> was later requested,
     * the message manager would attempt to load a resource bundle with
     * the path <code>rsrc.messages.game.chess</code> and would eventually
     * search for a file in the classpath with the path
     * <code>rsrc/messages/game/chess.properties</code>.
     *
     * <p> See the documentation for {@link
     * ResourceBundle#getBundle(String,Locale,ClassLoader)} for a more
     * detailed explanation of how resource bundle paths are resolved.
     */
    public function MessageManager ()
    {
        // load up the global bundle
        _global = getBundle(GLOBAL_BUNDLE);
    }

    /**
     * Fetches the message bundle for the specified path. If no bundle can
     * be located with the specified path, a special bundle is returned
     * that returns the untranslated message identifiers instead of an
     * associated translation. This is done so that error code to handle a
     * failed bundle load need not be replicated wherever bundles are
     * used. Instead an error will be logged and the requesting service
     * can continue to function in an impaired state.
     */
    public function getBundle (path :String) :MessageBundle
    {
        // first look in the cache
        var bundle :MessageBundle = (_cache.get(path) as MessageBundle);
        if (bundle != null) {
            return bundle;
        }

        // see if we should use a custom class for the bundle
        var mbclass :String = ResourceManager.getInstance().getString(path, MBUNDLE_CLASS_KEY);
        if (mbclass != null) {
            try {
                var clazz :Class = ClassUtil.getClassByName(String(mbclass));
                bundle = new clazz();
            } catch (ee :Error) {
                Log.getLog(this).warning("Failure instantiating custom message bundle",
                    "mbclass", mbclass, ee);
            }
        }
        // if there was no custom class, or we failed to instantiate the
        // custom class, use a standard message bundle
        if (bundle == null) {
            bundle = new MessageBundle();
        }

        // initialize our message bundle, cache it and return it (if we
        // couldn't resolve the bundle, the message bundle will cope with
        // it's null resource bundle)
        bundle.init(this, path, _global);
        _cache.put(path, bundle);
        return bundle;
    }

    /** The locale for which we're obtaining message bundles. */
    protected var _locale :String;

    /** A cache of instantiated message bundles. */
    protected var _cache :Map = Maps.newMapOf(String);

    /** Our top-level message bundle, from which others obtain messages if
     * they can't find them within themselves. */
    protected var _global :MessageBundle;

    /** A key that can contain the classname of a custom message bundle
     * class to be used to handle messages for a particular bundle. */
    protected static const MBUNDLE_CLASS_KEY :String = "msgbundle_class";
}
}
