package lsieun.asm.analysis;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

public class MockAnalyzer<V extends Value> implements Opcodes {
    private final Interpreter<V> interpreter;

    public MockAnalyzer(Interpreter<V> interpreter) {
        this.interpreter = interpreter;
    }

    @SuppressWarnings("unchecked")
    public Frame<V>[] analyze(String owner, MethodNode method) throws AnalyzerException {
        // 第一步，如果是abstract或native方法，则直接返回。
        if ((method.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            return (Frame<V>[]) new Frame<?>[0];
        }

        // 第二步，定义局部变量
        // （1）数据输入：获取指令集
        InsnList insnList = method.instructions;
        int size = insnList.size();

        // （2）中间状态：记录需要哪一个指令需要处理
        boolean[] instructionsToProcess = new boolean[size];

        // （3）数据输出：最终的返回结果
        Frame<V>[] frames = (Frame<V>[]) new Frame<?>[size];

        // 第三步，开始计算
        // （1）开始计算：根据方法的参数，计算方法的初始Frame
        Frame<V> currentFrame = computeInitialFrame(owner, method);
        merge(frames, 0, currentFrame, instructionsToProcess);

        // （2）开始计算：根据方法的每一条指令，计算相应的Frame
        while (getCount(instructionsToProcess) > 0) {
            // 获取需要处理的指令索引（insnIndex）和旧的Frame（oldFrame）
            int insnIndex = getFirst(instructionsToProcess);
            Frame<V> oldFrame = frames[insnIndex];
            instructionsToProcess[insnIndex] = false;

            // 模拟每一条指令的执行
            try {
                AbstractInsnNode insnNode = method.instructions.get(insnIndex);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();

                // 这三者并不是真正的指令，分别表示Label、LineNumberTable和Frame
                if (insnType == AbstractInsnNode.LABEL
                        || insnType == AbstractInsnNode.LINE
                        || insnType == AbstractInsnNode.FRAME) {
                    merge(frames, insnIndex + 1, oldFrame, instructionsToProcess);
                }
                else {
                    // 这里是真正的指令
                    currentFrame.init(oldFrame).execute(insnNode, interpreter);

                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode jumpInsn = (JumpInsnNode) insnNode;
                        // if之后的语句
                        if (insnOpcode != GOTO) {
                            merge(frames, insnIndex + 1, currentFrame, instructionsToProcess);
                        }

                        // if和goto跳转之后的位置
                        int jumpInsnIndex = insnList.indexOf(jumpInsn.label);
                        merge(frames, jumpInsnIndex, currentFrame, instructionsToProcess);
                    }
                    else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode) insnNode;

                        // lookupswitch的default情况
                        int targetInsnIndex = insnList.indexOf(lookupSwitchInsn.dflt);
                        merge(frames, targetInsnIndex, currentFrame, instructionsToProcess);

                        // lookupswitch的各种case情况
                        for (int i = 0; i < lookupSwitchInsn.labels.size(); ++i) {
                            LabelNode label = lookupSwitchInsn.labels.get(i);
                            targetInsnIndex = insnList.indexOf(label);
                            merge(frames, targetInsnIndex, currentFrame, instructionsToProcess);
                        }
                    }
                    else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode) insnNode;

                        // tableswitch的default情况
                        int targetInsnIndex = insnList.indexOf(tableSwitchInsn.dflt);
                        merge(frames, targetInsnIndex, currentFrame, instructionsToProcess);

                        // tableswitch的各种case情况
                        for (int i = 0; i < tableSwitchInsn.labels.size(); ++i) {
                            LabelNode label = tableSwitchInsn.labels.get(i);
                            targetInsnIndex = insnList.indexOf(label);
                            merge(frames, targetInsnIndex, currentFrame, instructionsToProcess);
                        }
                    }
                    else if (insnOpcode != ATHROW && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                        merge(frames, insnIndex + 1, currentFrame, instructionsToProcess);
                    }
                }
            }
            catch (AnalyzerException e) {
                throw new AnalyzerException(e.node, "Error at instruction " + insnIndex + ": " + e.getMessage(), e);
            }
        }

        return frames;
    }

    private int getCount(boolean[] array) {
        int count = 0;
        for (boolean flag : array) {
            if (flag) {
                count++;
            }
        }
        return count;
    }

    private int getFirst(boolean[] array) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            boolean flag = array[i];
            if (flag) {
                return i;
            }
        }
        return -1;
    }

    private Frame<V> computeInitialFrame(final String owner, final MethodNode method) {
        Frame<V> frame = new Frame<>(method.maxLocals, method.maxStack);
        int currentLocal = 0;

        // 第一步，判断是否需要存储this变量
        boolean isInstanceMethod = (method.access & ACC_STATIC) == 0;
        if (isInstanceMethod) {
            Type ownerType = Type.getObjectType(owner);
            V value = interpreter.newParameterValue(isInstanceMethod, currentLocal, ownerType);
            frame.setLocal(currentLocal, value);
            currentLocal++;
        }

        // 第二步，将方法的参数存入到local variable内
        Type[] argumentTypes = Type.getArgumentTypes(method.desc);
        for (Type argumentType : argumentTypes) {
            V value = interpreter.newParameterValue(isInstanceMethod, currentLocal, argumentType);
            frame.setLocal(currentLocal, value);
            currentLocal++;
            if (argumentType.getSize() == 2) {
                frame.setLocal(currentLocal, interpreter.newEmptyValue(currentLocal));
                currentLocal++;
            }
        }

        // 第三步，将local variable的剩余位置填补上空值
        while (currentLocal < method.maxLocals) {
            frame.setLocal(currentLocal, interpreter.newEmptyValue(currentLocal));
            currentLocal++;
        }

        // 第四步，设置返回值类型
        frame.setReturn(interpreter.newReturnTypeValue(Type.getReturnType(method.desc)));
        return frame;
    }

    /**
     * Merge old frame with new frame.
     *
     * @param frames 所有的frame信息。
     * @param insnIndex 当前指令的索引。
     * @param newFrame 新的frame
     * @param instructionsToProcess 记录哪一条指令需要处理
     * @throws AnalyzerException 分析错误，抛出此异常
     */
    private void merge(Frame<V>[] frames, int insnIndex, Frame<V> newFrame, boolean[] instructionsToProcess) throws AnalyzerException {
        boolean changed;
        Frame<V> oldFrame = frames[insnIndex];
        if (oldFrame == null) {
            frames[insnIndex] = new Frame<>(newFrame);
            changed = true;
        }
        else {
            changed = oldFrame.merge(newFrame, interpreter);
        }

        if (changed && !instructionsToProcess[insnIndex]) {
            instructionsToProcess[insnIndex] = true;
        }
    }
}
