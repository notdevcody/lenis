package pl.tomgirl.pylon.forge;

import net.minecraft.launchwrapper.IClassTransformer;
import pl.tomgirl.pylon.game.GameTransformer;

public final class PylonTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytecode) {
        if (bytecode == null || name.startsWith("pl.tomgirl.pylon.") || name.startsWith("org.lwjgl.")) {
            return bytecode;
        }
        return GameTransformer.transform(bytecode);
    }
}
