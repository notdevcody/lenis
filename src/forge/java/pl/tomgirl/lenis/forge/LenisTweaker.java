package pl.tomgirl.lenis.forge;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

@SuppressWarnings("unused")
public final class LenisTweaker implements ITweaker {
    private LaunchClassLoader classLoader;

    @Override
    public void acceptOptions(List<String> arguments, File gameDirectory, File assetsDirectory, String profile) {
        System.setProperty("java.awt.headless", "true");
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        this.classLoader = classLoader;
        try {
            Field parent = classLoader.getClass().getDeclaredField("parent");
            parent.setAccessible(true);
            ClassLoader launcherParent = (ClassLoader) parent.get(classLoader);
            URL runtime = LenisTweaker.class.getProtectionDomain().getCodeSource().getLocation();
            RuntimeClassLoader runtimeLoader = new RuntimeClassLoader(runtime, launcherParent);
            parent.set(classLoader, runtimeLoader);
            if (System.getProperty("os.name", "").startsWith("Mac")) {
                Class<?> display = runtimeLoader.loadClass("pl.tomgirl.lenis.window.DisplaySdl");
                Object instance = display.getMethod("instance").invoke(null);
                display.getMethod("initializeVideo").invoke(instance);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to install the Lenis runtime class loader", e);
        }

        classLoader.addClassLoaderExclusion("pl.tomgirl.lenis.");
        classLoader.registerTransformer(LenisTransformer.class.getName());
    }

    private static class RuntimeClassLoader extends URLClassLoader {
        RuntimeClassLoader(URL runtime, ClassLoader parent) {
            super(new URL[]{runtime}, parent);
        }

        @Override
        protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (name.startsWith("org.lwjgl.") || name.startsWith("pl.tomgirl.lenis.")) {
                Class<?> loaded = findLoadedClass(name);
                if (loaded == null) {
                    try {
                        loaded = findClass(name);
                    } catch (ClassNotFoundException ignored) {
                        loaded = super.loadClass(name, false);
                    }
                }

                if (resolve) {
                    resolveClass(loaded);
                }

                return loaded;
            }

            return super.loadClass(name, resolve);
        }
    }

    @Override
    public String getLaunchTarget() {
        return "";
    }

    @Override
    public String[] getLaunchArguments() {
        classLoader.addClassLoaderExclusion("org.lwjgl.");
        return new String[0];
    }
}
