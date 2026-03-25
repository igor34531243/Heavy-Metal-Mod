package ASM_Igor;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import static ASM_Igor.ElnCorePlugin.print_debug_igor_asm;

public class ElnNodeTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("mods.eln.node.transparent.TransparentNodeItem")) {
            if (print_debug_igor_asm) {
                System.out.println("found transparent node item!!!");
            }
            return patchPlaceBlockAt(basicClass,"mods/eln/node/transparent/TransparentNode","com/steve1/igortweakseaaddon/misc/IgorNode/IgorTransparentNode/IgorTransparentNode");
        } else if (transformedName.equals("mods.eln.node.six.SixNodeItem")) {
            if (print_debug_igor_asm) {
                System.out.println("found six node item!!!");
            }
            return patchPlaceBlockAt(basicClass,"mods/eln/node/six/SixNode","com/steve1/igortweakseaaddon/misc/IgorNode/IgorSixNode/IgorSixNode");
        }
        return basicClass;
    }

    private byte[] patchPlaceBlockAt(byte[] basicClass, String classs_to_remove, String class_to_set) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            if (print_debug_igor_asm) {
                System.out.println("found method: " + method.name + " !!!");
            }
            if (method.name.equals("placeBlockAt") || method.name.equals("func_150941_a")) {
                if (print_debug_igor_asm) {
                    System.out.println("we found a method to patch!!!");
                }
                for (AbstractInsnNode insn : method.instructions.toArray()) {

                    if (insn.getOpcode() == Opcodes.NEW) {
                        TypeInsnNode typeInsn = (TypeInsnNode) insn;
                        if (typeInsn.desc.equals(classs_to_remove)) {
                            typeInsn.desc = class_to_set;
                        }
                    }

                    if (insn.getOpcode() == Opcodes.INVOKESPECIAL) {
                        MethodInsnNode methInsn = (MethodInsnNode) insn;
                        if (methInsn.owner.equals(classs_to_remove) && methInsn.name.equals("<init>")) {
                            methInsn.owner = class_to_set;
                        }
                    }
                }
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }
}
