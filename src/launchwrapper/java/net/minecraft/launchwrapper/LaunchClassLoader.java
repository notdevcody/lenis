package net.minecraft.launchwrapper;

public abstract class LaunchClassLoader extends ClassLoader {
    private LaunchClassLoader() {}
    public native void registerTransformer(String name);
    public native void addClassLoaderExclusion(String prefix);
}
