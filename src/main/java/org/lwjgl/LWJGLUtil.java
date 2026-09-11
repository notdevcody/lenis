package org.lwjgl;

import pl.tomgirl.lenis.Platform;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class LWJGLUtil {
    public static final boolean DEBUG = Boolean.getBoolean("org.lwjgl.util.Debug");
    private static final Logger LOG = Logger.getLogger("LWJGL");

    private LWJGLUtil() {}

    public static void log(CharSequence msg) {
        if (DEBUG) {
            LOG.log(Level.FINE, msg.toString());
        }
    }

    public static int getPlatform() {
        return Platform.CURRENT.ordinal() + 1;
    }

    public static String getPlatformName() {
        switch (Platform.CURRENT) {
            case UNIX:
                return "linux";
            case MACOS:
                return "macosx";
            case WINDOWS:
                return "windows";
            default:
                return "unknown";
        }
    }
}
