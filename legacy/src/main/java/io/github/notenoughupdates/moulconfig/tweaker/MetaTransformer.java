package io.github.notenoughupdates.moulconfig.tweaker;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

public class MetaTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name.equals("net.minecraftforge.fml.common.discovery.ModDiscoverer")) {
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new ClassWriter(1);
            ClassVisitor visitor = new ModDiscovererTransformer(writer);
            reader.accept(visitor, 0);
            return writer.toByteArray();
        }
        return basicClass;
    }
}
