package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.analysis.Frame;

public class NullabilityFrame extends Frame<NullabilityValue> {
    public NullabilityFrame(int numLocals, int numStack) {
        super(numLocals, numStack);
    }

    public NullabilityFrame(NullabilityFrame frame) {
        super(frame);
    }

    @Override
    public void initJumpTarget(int opcode, LabelNode target) {
        // 首先，处理自己的代码逻辑
        int stackIndex = getStackSize();
        NullabilityValue oldValue = getStack(stackIndex);
        switch (opcode) {
            case Opcodes.IFNULL: {
                if (target == null) {
                    updateFrame(oldValue, Nullability.NOT_NULL);
                }
                else {
                    updateFrame(oldValue, Nullability.NULL);
                }
                break;
            }
            case Opcodes.IFNONNULL: {
                if (target == null) {
                    updateFrame(oldValue, Nullability.NULL);
                }
                else {
                    updateFrame(oldValue, Nullability.NOT_NULL);
                }
                break;
            }
        }

        // 其次，调用父类的方法实现
        super.initJumpTarget(opcode, target);
    }

    private void updateFrame(NullabilityValue oldValue, Nullability newState) {
        NullabilityValue newValue = new NullabilityValue(oldValue.getType(), newState);
        int numLocals = getLocals();
        for (int i = 0; i < numLocals; i++) {
            NullabilityValue currentValue = getLocal(i);
            if (oldValue == currentValue) {
                setLocal(i, newValue);
            }
        }

        int numStack = getMaxStackSize();
        for (int i = 0; i < numStack; i++) {
            NullabilityValue currentValue = getStack(i);
            if (oldValue == currentValue) {
                setStack(i, newValue);
            }
        }
    }
}
