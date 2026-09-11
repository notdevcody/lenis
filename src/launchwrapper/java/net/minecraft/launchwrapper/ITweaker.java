package net.minecraft.launchwrapper;

import java.io.File;
import java.util.List;

public interface ITweaker {
    void acceptOptions(List<String> arguments, File gameDirectory, File assetsDirectory, String profile);
    void injectIntoClassLoader(LaunchClassLoader classLoader);
    String getLaunchTarget();
    String[] getLaunchArguments();
}
