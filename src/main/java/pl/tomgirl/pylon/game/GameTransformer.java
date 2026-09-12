package pl.tomgirl.pylon.game;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.logging.Level;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import pl.tomgirl.pylon.Pylon;
import pl.tomgirl.pylon.game.patch.Patch;
import pl.tomgirl.pylon.game.patch.impl.*;

public final class GameTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String name, Class<?> type, ProtectionDomain domain, byte[] bytecode) {
        return transform(bytecode);
    }

    public static byte[] transform(byte[] bytecode) {
        FmlModContainerPatch fml = new FmlModContainerPatch(null);
        ScreenPatch screen = new ScreenPatch(fml);
        MinecraftPatch minecraft = new MinecraftPatch(screen);
        ClassReader reader = new ClassReader(bytecode);
        reader.accept(minecraft, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        List<Patch> matches = Stream.of(minecraft, screen, fml).filter(Patch::matches).collect(Collectors.toList());
        if (matches.isEmpty()) return bytecode;

        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor patcher = writer;
        for (Patch patch : matches) {
            patch.applied();
            patcher = patch.apply(patcher);
        }
        reader.accept(patcher, 0);
        Pylon.LOG.log(Level.FINE, "Applied {0} to {1}", new Object[]{matches, reader.getClassName()});
        return writer.toByteArray();
    }
}
