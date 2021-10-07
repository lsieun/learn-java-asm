package lsieun.asm.analysis.nullability;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.ArrayList;
import java.util.List;

import static lsieun.asm.analysis.nullability.NullabilityValue.*;

public class NullabilityFrame extends Frame<NullabilityValue> {
    private static final String START = "{";
    private static final String STOP = "}";
    private static final String EMPTY = "{}";
    private static final String SEPARATOR = "|";

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
                    updateFrame(oldValue, NOT_NULL_VALUE);
                }
                else {
                    updateFrame(oldValue, NULL_VALUE);
                }
                break;
            }
            case Opcodes.IFNONNULL: {
                if (target == null) {
                    updateFrame(oldValue, NULL_VALUE);
                }
                else {
                    updateFrame(oldValue, NOT_NULL_VALUE);
                }
                break;
            }
        }

        // 其次，调用父类的方法实现
        super.initJumpTarget(opcode, target);
    }

    private void updateFrame(NullabilityValue oldValue, NullabilityValue newValue) {
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

    @Override
    public String toString() {
        List<NullabilityValue> localList = new ArrayList<>();
        int maxLocals = getLocals();
        for (int i = 0; i < maxLocals; i++) {
            localList.add(getLocal(i));
        }

        List<NullabilityValue> stackList = new ArrayList<>();
        int maxStack = getStackSize();
        for (int i = 0; i < maxStack; i++) {
            stackList.add(getStack(i));
        }

        String locals_str = list2Str(localList);
        String stack_str = list2Str(stackList);
        return String.format("%s %s %s", locals_str, SEPARATOR, stack_str);
    }

    private String list2Str(List<NullabilityValue> list) {
        if (list == null || list.size() == 0) return EMPTY;
        int size = list.size();
        String[] array = new String[size];
        for (int i = 0; i < size - 1; i++) {
            NullabilityValue item = list.get(i);
            array[i] = item2Str(item);
        }

        {
            // 最后一个值
            int lastIndex = size - 1;
            NullabilityValue item = list.get(lastIndex);
            array[lastIndex] = item2Str(item);
        }

        return array2Str(array);
    }

    private String array2Str(String[] array) {
        if (array == null || array.length == 0) return EMPTY;
        int length = array.length;

        StringBuilder sb = new StringBuilder();
        sb.append(START);
        for (int i = 0; i < length - 1; i++) {
            sb.append(array[i]).append(", ");
        }
        sb.append(array[length - 1]);
        sb.append(STOP);
        return sb.toString();
    }

    private String item2Str(NullabilityValue value) {
        if (value == null) {
            return "null";
        }
        else if (value == UNINITIALIZED_VALUE) {
            return ".";
        }
        else if (value == RETURN_ADDRESS_VALUE) {
            return "A";
        }
        else if (value == UNKNOWN_VALUE) {
            return "unknown";
        }
        else if (value == NULL_VALUE) {
            return "null";
        }
        else if (value == NOT_NULL_VALUE) {
            return "not-null";
        }
        else if (value == NULLABLE_VALUE) {
            return "nullable";
        }
        else {
            return value.getType().getClassName();
        }
    }
}
