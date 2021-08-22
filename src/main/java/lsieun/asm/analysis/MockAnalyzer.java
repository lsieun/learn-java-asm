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
    private InsnList insnList;
    private int insnListSize;
    private Frame<V>[] frames;

    private boolean[] inInstructionsToProcess;
    private int[] instructionsToProcess;
    private int numInstructionsToProcess;

    public MockAnalyzer(final Interpreter<V> interpreter) {
        this.interpreter = interpreter;
    }

    @SuppressWarnings("unchecked")
    public Frame<V>[] analyze(final String owner, final MethodNode method) throws AnalyzerException {
        if ((method.access & (ACC_ABSTRACT | ACC_NATIVE)) != 0) {
            frames = (Frame<V>[]) new Frame<?>[0];
            return frames;
        }

        insnList = method.instructions;
        insnListSize = insnList.size();
        frames = (Frame<V>[]) new Frame<?>[insnListSize];
        inInstructionsToProcess = new boolean[insnListSize];
        instructionsToProcess = new int[insnListSize];
        numInstructionsToProcess = 0;

        // Initializes the data structures for the control flow analysis.
        Frame<V> currentFrame = computeInitialFrame(owner, method);
        merge(0, currentFrame);

        while (numInstructionsToProcess > 0) {
            // Get and remove one instruction from the list of instructions to process.
            int insnIndex = instructionsToProcess[--numInstructionsToProcess];
            Frame<V> oldFrame = frames[insnIndex];
            inInstructionsToProcess[insnIndex] = false;

            // Simulate the execution of this instruction.
            AbstractInsnNode insnNode = null;
            try {
                insnNode = method.instructions.get(insnIndex);
                int insnOpcode = insnNode.getOpcode();
                int insnType = insnNode.getType();

                if (insnType == AbstractInsnNode.LABEL
                        || insnType == AbstractInsnNode.LINE
                        || insnType == AbstractInsnNode.FRAME) {
                    merge(insnIndex + 1, oldFrame);
                }
                else {
                    currentFrame.init(oldFrame).execute(insnNode, interpreter);

                    if (insnNode instanceof JumpInsnNode) {
                        JumpInsnNode jumpInsn = (JumpInsnNode) insnNode;
                        // if之后的语句
                        if (insnOpcode != GOTO) {
                            merge(insnIndex + 1, currentFrame);
                        }

                        // if和goto跳转之后的位置
                        int jumpInsnIndex = insnList.indexOf(jumpInsn.label);
                        merge(jumpInsnIndex, currentFrame);
                    }
                    else if (insnNode instanceof LookupSwitchInsnNode) {
                        LookupSwitchInsnNode lookupSwitchInsn = (LookupSwitchInsnNode) insnNode;

                        // lookupswitch的default情况
                        int targetInsnIndex = insnList.indexOf(lookupSwitchInsn.dflt);
                        merge(targetInsnIndex, currentFrame);

                        // lookupswitch的各种case情况
                        for (int i = 0; i < lookupSwitchInsn.labels.size(); ++i) {
                            LabelNode label = lookupSwitchInsn.labels.get(i);
                            targetInsnIndex = insnList.indexOf(label);
                            merge(targetInsnIndex, currentFrame);
                        }
                    }
                    else if (insnNode instanceof TableSwitchInsnNode) {
                        TableSwitchInsnNode tableSwitchInsn = (TableSwitchInsnNode) insnNode;

                        // tableswitch的default情况
                        int targetInsnIndex = insnList.indexOf(tableSwitchInsn.dflt);
                        merge(targetInsnIndex, currentFrame);

                        // tableswitch的各种case情况
                        for (int i = 0; i < tableSwitchInsn.labels.size(); ++i) {
                            LabelNode label = tableSwitchInsn.labels.get(i);
                            targetInsnIndex = insnList.indexOf(label);
                            merge(targetInsnIndex, currentFrame);
                        }
                    }
                    else if (insnOpcode != ATHROW && (insnOpcode < IRETURN || insnOpcode > RETURN)) {
                        merge(insnIndex + 1, currentFrame);
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

    private void merge(final int insnIndex, final Frame<V> frame) throws AnalyzerException {
        boolean changed;
        Frame<V> oldFrame = frames[insnIndex];
        if (oldFrame == null) {
            frames[insnIndex] = new Frame<>(frame);
            changed = true;
        }
        else {
            changed = oldFrame.merge(frame, interpreter);
        }

        if (changed && !inInstructionsToProcess[insnIndex]) {
            inInstructionsToProcess[insnIndex] = true;
            instructionsToProcess[numInstructionsToProcess++] = insnIndex;
        }
    }
}
