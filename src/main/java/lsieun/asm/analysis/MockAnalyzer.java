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
        if ((method.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            return (Frame<V>[]) new Frame<?>[0];
        }

        InsnList insnList = method.instructions;
        int size = insnList.size();
        Frame<V>[] frames = (Frame<V>[]) new Frame<?>[size];
        boolean[] instructionsToProcess = new boolean[size];

        // Initializes the data structures for the control flow analysis.
        Frame<V> currentFrame = computeInitialFrame(owner, method);
        merge(frames, 0, currentFrame, instructionsToProcess);

        while (getCount(instructionsToProcess) > 0) {
            // Get and remove one instruction from the list of instructions to process.
            int insnIndex = getFirst(instructionsToProcess);
            Frame<V> oldFrame = frames[insnIndex];
            instructionsToProcess[insnIndex] = false;

            // Simulate the execution of this instruction.
            AbstractInsnNode insnNode = null;
            try {
                insnNode = method.instructions.get(insnIndex);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();

                if (insnType == AbstractInsnNode.LABEL
                        || insnType == AbstractInsnNode.LINE
                        || insnType == AbstractInsnNode.FRAME) {
                    merge(frames, insnIndex + 1, oldFrame, instructionsToProcess);
                }
                else {
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
            catch (RuntimeException e) {
                // DontCheck(IllegalCatch): can't be fixed, for backward compatibility.
                throw new AnalyzerException(insnNode, "Error at instruction " + insnIndex + ": " + e.getMessage(), e);
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
        boolean isInstanceMethod = (method.access & ACC_STATIC) == 0;
        if (isInstanceMethod) {
            Type ownerType = Type.getObjectType(owner);
            V value = interpreter.newParameterValue(isInstanceMethod, currentLocal, ownerType);
            frame.setLocal(currentLocal, value);
            currentLocal++;
        }

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

        while (currentLocal < method.maxLocals) {
            frame.setLocal(currentLocal, interpreter.newEmptyValue(currentLocal));
            currentLocal++;
        }
        frame.setReturn(interpreter.newReturnTypeValue(Type.getReturnType(method.desc)));
        return frame;
    }

    private void merge(Frame<V>[] frames, int insnIndex, Frame<V> frame, boolean[] instructionsToProcess) throws AnalyzerException {
        boolean changed;
        Frame<V> oldFrame = frames[insnIndex];
        if (oldFrame == null) {
            frames[insnIndex] = new Frame<>(frame);
            changed = true;
        }
        else {
            changed = oldFrame.merge(frame, interpreter);
        }

        if (changed && !instructionsToProcess[insnIndex]) {
            instructionsToProcess[insnIndex] = true;
        }
    }
}
