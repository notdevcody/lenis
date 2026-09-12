package pl.tomgirl.pylon.game.patch;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import pl.tomgirl.pylon.game.GameHooks;

public abstract class Patch extends ClassVisitor {
    public static final String HOOKS = Type.getInternalName(GameHooks.class);

    public Patch(ClassVisitor next) { super(Opcodes.ASM9, next); }

    public abstract boolean matches();
    public abstract ClassVisitor apply(ClassVisitor writer);
    public void applied() {}

    @Override
    public final String toString() { return getClass().getSimpleName(); }

    public static final class Method {
        private final String name;
        private final String descriptor;

        public Method(String name, String descriptor) {
            this.name = name;
            this.descriptor = descriptor;
        }

        public String name() { return name; }
        public String descriptor() { return descriptor; }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Method)) return false;
            Method method = (Method) other;
            return name.equals(method.name) && descriptor.equals(method.descriptor);
        }

        @Override
        public int hashCode() { return name.hashCode() * 31 + descriptor.hashCode(); }

        @Override
        public String toString() { return name + descriptor; }
    }
}
