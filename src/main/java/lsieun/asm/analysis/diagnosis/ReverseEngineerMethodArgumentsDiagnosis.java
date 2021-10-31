package lsieun.asm.analysis.diagnosis;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;

public class ReverseEngineerMethodArgumentsDiagnosis {
    private static final String UNKNOWN_VARIABLE_NAME = "unknown";

    public static void diagnose(String className, MethodNode mn) throws AnalyzerException {
        // 第一步，获取Frame信息
        Analyzer<SourceValue> analyzer = new Analyzer<>(new SourceInterpreter());
        Frame<SourceValue>[] frames = analyzer.analyze(className, mn);

        // 第二步，获取LocalVariableTable信息
        List<LocalVariableNode> localVariables = mn.localVariables;
        if (localVariables == null || localVariables.size() < 1) {
            System.out.println("LocalVariableTable is Empty");
            return;
        }

        // 第三步，获取instructions，并找到与invoke相关的指令
        InsnList instructions = mn.instructions;
        int[] methodInsnArray = findMethodInvokes(instructions);

        // 第四步，对invoke相关的指令进行反编译
        for (int methodInsn : methodInsnArray) {
            // (1) 获取方法的参数
            MethodInsnNode methodInsnNode = (MethodInsnNode) instructions.get(methodInsn);
            Type methodType = Type.getMethodType(methodInsnNode.desc);
            Type[] argumentTypes = methodType.getArgumentTypes();
            int argNum = argumentTypes.length;

            // (2) 从Frame当中获取指令，并将指令转换LocalVariableTable当中的变量名
            Frame<SourceValue> f = frames[methodInsn];
            int stackSize = f.getStackSize();
            List<String> argList = new ArrayList<>();
            for (int i = 0; i < argNum; i++) {
                int stackIndex = stackSize - argNum + i;
                SourceValue stackValue = f.getStack(stackIndex);
                AbstractInsnNode insn = stackValue.insns.iterator().next();
                String argName = getMethodVariableName(insn, localVariables);
                argList.add(argName);
            }

            // (3) 将反编译的结果打印出来
            String line = String.format("%s.%s(%s)", methodInsnNode.owner, methodInsnNode.name, argList);
            System.out.println(line);
        }
    }

    public static String getMethodVariableName(AbstractInsnNode insn, List<LocalVariableNode> localVariables) {
        if (insn instanceof VarInsnNode) {
            VarInsnNode varInsnNode = (VarInsnNode) insn;
            int localIndex = varInsnNode.var;

            for (LocalVariableNode node : localVariables) {
                if (node.index == localIndex) {
                    return node.name;
                }
            }

            return String.format("locals[%d]", localIndex);
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
