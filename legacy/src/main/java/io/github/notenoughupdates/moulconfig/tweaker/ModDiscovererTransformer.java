package io.github.notenoughupdates.moulconfig.tweaker;

import io.github.notenoughupdates.moulconfig.internal.RPModContainer;
import lombok.var;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ModDiscovererTransformer extends ClassVisitor {
    public ModDiscovererTransformer(ClassWriter writer) {
        super(Opcodes.ASM5, writer);
    }

    @Override
    public MethodVisitor visitMethod(int mAccess, String name, String desc, String signature, String[] exceptions) {
        var sup = super.visitMethod(mAccess, name, desc, signature, exceptions);
        if (name.equals("identifyMods")) {
            return new MethodVisitor(Opcodes.ASM5, sup) {
                @Override
                public void visitCode() {
                    super.visitCode();
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitFieldInsn(
                        Opcodes.GETFIELD,
                        "net/minecraftforge/fml/common/discovery/ModDiscoverer",
                        "candidates",
                        "Ljava/util/List;"
                    );
                    visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        RPModContainer.class.getName().replace(".", "/"),
                        "injectModContainer",
                        "(Ljava/util/List;)V",
                        false
                    );
                }
            };
        }
        return sup;
    }
}
