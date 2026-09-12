package pl.tomgirl.pylon.game.patch.impl;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import pl.tomgirl.pylon.game.patch.Patch;

public class FmlModContainerPatch extends Patch {
    private String owner;
    private String container;
    private Method identifyMods;

    public FmlModContainerPatch(ClassVisitor next) {
        super(next);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String parent, String[] interfaces) {
        owner = name;
        if (name.equals("net/minecraftforge/fml/common/Loader")) {
            container = "pl.tomgirl.pylonfml.PylonModContainer";
        } else if (name.equals("cpw/mods/fml/common/Loader")) {
            container = "pl.tomgirl.pylonfml.CpwModContainer";
        }
        super.visit(version, access, name, signature, parent, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (container != null && name.equals("identifyMods") && (access & Opcodes.ACC_STATIC) == 0) {
            String discoverer = "L" + owner.substring(0, owner.lastIndexOf('/') + 1) + "discovery/ModDiscoverer;";
            if (descriptor.equals("()" + discoverer) || descriptor.equals("(Ljava/util/List;)" + discoverer)) {
                identifyMods = new Method(name, descriptor);
            }
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public boolean matches() {
        return identifyMods != null;
    }

    @Override
    public ClassVisitor apply(ClassVisitor writer) {
        return new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor method = super.visitMethod(access, name, descriptor, signature, exceptions);
                if (!new Method(name, descriptor).equals(identifyMods)) {
                    return method;
                }
                return new MethodVisitor(Opcodes.ASM9, method) {
                    @Override
                    public void visitCode() {
                        super.visitCode();
                        super.visitFieldInsn(Opcodes.GETSTATIC, owner, "injectedContainers", "Ljava/util/List;");
                        super.visitLdcInsn(container);
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
                        super.visitInsn(Opcodes.POP);
                    }

                    @Override
                    public void visitMaxs(int maxStack, int maxLocals) {
                        super.visitMaxs(Math.max(maxStack, 2), maxLocals);
                    }
                };
            }
        };
    }
}
