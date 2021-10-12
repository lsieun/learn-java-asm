package lsieun.asm.analysis;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;

public class ReverseEngineerMethodInvokeAnalyzer {
    private static final String UNKNOWN_VARIABLE_NAME = "unknown";

    public static void analyze(String className, MethodNode mn) throws AnalyzerException {
        Analyzer<SourceValue> a = new Analyzer<>(new SourceInterpreter());
        Frame<SourceValue>[] frames = a.analyze(className, mn);
        InsnList instructions = mn.instructions;
        List<LocalVariableNode> localVariables = mn.localVariables;

        int[] methodInsnArray = findMethodInvokes(instructions);
        for (int methodInsn : methodInsnArray) {
            MethodInsnNode methodInsnNode = (MethodInsnNode) instructions.get(methodInsn);
            Type methodType = Type.getMethodType(methodInsnNode.desc);
            Type[] argumentTypes = methodType.getArgumentTypes();
            int argNum = argumentTypes.length;

            Frame<SourceValue> f = frames[methodInsn];
            int stackSize = f.getStackSize();
            List<String> argList = new ArrayList<>();
            for (int i = 0; i < argNum; i++) {
                String argName = getMethodVariableName(f, stackSize - argNum + i, localVariables);
                argList.add(argName);
            }

            String line = String.format("%s.%s(%s)", methodInsnNode.owner, methodInsnNode.name, argList);
            System.out.println(line);
        }
    }

    public static String getMethodVariableName(Frame<SourceValue> f, int stackIndex, List<LocalVariableNode> localVariables) {
        SourceValue stack = f.getStack(stackIndex);
        AbstractInsnNode insn = stack.insns.iterator().next();
        if (insn instanceof VarInsnNode) {
            VarInsnNode varInsnNode = (VarInsnNode) insn;
            int var = varInsnNode.var;
            LocalVariableNode localVariableNode = localVariables.get(var);
            return localVariableNode.name;
        }
        return UNKNOWN_VARIABLE_NAME;
    }

    public static int[] findMethodInvokes(InsnList instructions) {
        int size = instructions.size();
        boolean[] methodArray = new boolean[size];
        for (int i = 0; i < size; i++) {
            AbstractInsnNode node = instructions.get(i);
            if (node instanceof MethodInsnNode) {
                methodArray[i] = true;
            }
        }

        int count = 0;
        for (boolean flag : methodArray) {
            if (flag) {
                count++;
            }
        }

        int[] array = new int[count];
        int j = 0;
        for (int i = 0; i < size; i++) {
            boolean flag = methodArray[i];
            if (flag) {
                array[j] = i;
                j++;
            }
        }
        return array;
    }
}
