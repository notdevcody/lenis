package pl.tomgirl.pylon.forge;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class OldFmlCompat {
    private OldFmlCompat() {}

    public static void prepare(ClassLoader loader) {
        String remapperName = "cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper";
        if (loader.getResource(remapperName.replace('.', '/') + ".class") == null) {
            return;
        }

        try {
            try {
                Class.forName("org.objectweb.asm.Opcodes", false, loader).getField("ASM5");
                return;
            } catch (NoSuchFieldException ignored) {}

            Class<?> type = Class.forName(remapperName, true, loader);
            Object remapper = type.getField("INSTANCE").get(null);
            Map<String, Map<String, String>> methods = mappings(type, remapper, "methodNameMaps");
            Map<String, Map<String, String>> fields = mappings(type, remapper, "fieldNameMaps");
            File source = new File(OldFmlCompat.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (source.isDirectory()) {
                Path root = source.toPath();
                try (Stream<Path> paths = Files.walk(root)) {
                    paths.filter(Files::isRegularFile).forEach(path ->
                        exclude(root.relativize(path).toString().replace(File.separatorChar, '/'), methods, fields));
                }
            } else {
                try (JarFile jar = new JarFile(source)) {
                    jar.stream().forEach(entry -> exclude(entry.getName(), methods, fields));
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not configure legacy FML remapping", e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, String>> mappings(Class<?> type, Object remapper, String name)
        throws ReflectiveOperationException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return (Map<String, Map<String, String>>) field.get(remapper);
    }

    private static void exclude(String entry, Map<String, Map<String, String>> methods, Map<String, Map<String, String>> fields) {
        if (!entry.endsWith(".class") || !(entry.startsWith("org/lwjgl/") || entry.startsWith("pl/tomgirl/pylon/"))) {
            return;
        }
        String name = entry.substring(0, entry.length() - ".class".length());
        methods.put(name, Collections.emptyMap());
        fields.put(name, Collections.emptyMap());
    }
}
