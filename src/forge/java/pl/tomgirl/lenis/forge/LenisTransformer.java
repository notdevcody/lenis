package pl.tomgirl.lenis.forge;

import net.minecraft.launchwrapper.IClassTransformer;
import pl.tomgirl.lenis.game.GameTransformer;

public final class LenisTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] bytecode) {
        if (bytecode == null || name.startsWith("pl.tomgirl.lenis.") || name.startsWith("org.lwjgl.")) {
            return bytecode;
        }
        return GameTransformer.transform(bytecode);
    }
}
