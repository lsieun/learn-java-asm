package lsieun.utils;

import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

public class ASMUtilsTree {
    private static final int NOT_EXIST = -1;

    public static String getVariableName(String className, MethodNode mn, int line, String methodName, int param) throws AnalyzerException {
        Analyzer<SourceValue> a = new Analyzer<>(new SourceInterpreter());
        Frame<SourceValue>[] frames = a.analyze(className, mn);
        InsnList instructions = mn.instructions;
        LabelNode label = findLineLabel(instructions, line);
        int methodInsnIndex = findMethodCall(instructions, label, methodName);
        SourceValue stack = frames[methodInsnIndex].getStack(param);
        AbstractInsnNode insn = stack.insns.iterator().next();
        if (insn instanceof VarInsnNode) {
            VarInsnNode varInsnNode = (VarInsnNode) insn;
            int var = varInsnNode.var;
            LocalVariableNode localVariableNode = mn.localVariables.get(var);
            return localVariableNode.name;
        }
        return null;
    }

    public static LabelNode findLineLabel(InsnList instructions, int line) {
        for (AbstractInsnNode node : instructions) {
            if (node instanceof LineNumberNode) {
                LineNumberNode lineNumberNode = (LineNumberNode) node;
                if (lineNumberNode.line == line) {
                    return lineNumberNode.start;
                }
            }
        }
        return null;
    }

    public static int findMethodCall(InsnList instructions, LabelNode label, String name) {
        if (!instructions.contains(label)) {
            return NOT_EXIST;
        }
        int index = instructions.indexOf(label);
        index++;
        int size = instructions.size();
        while (index < size) {
            AbstractInsnNode node = instructions.get(index);
            if (node instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) node;
                if (methodInsnNode.name.equals(name)) {
                    return index;
                }
            }
            index++;
        }

        return NOT_EXIST;
    }
}
