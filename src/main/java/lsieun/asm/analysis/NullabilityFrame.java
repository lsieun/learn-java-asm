package lsieun.asm.analysis;

import jdk.internal.org.objectweb.asm.Opcodes;
import lsieun.asm.analysis.state.LocalStackStateFrame;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.analysis.BasicValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NullabilityFrame extends LocalStackStateFrame<BasicValue> {
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
        int index = getStackTopIndex() + 1;
        switch (opcode) {
            case Opcodes.IFNULL: {
                if (target == null) {
                    updateFrame(index, NullabilityInterpreter.NOT_NULL_VALUE);
                }
                else {
                    updateFrame(index, NullabilityInterpreter.NULL_VALUE);
                }
                break;
            }
            case Opcodes.IFNONNULL: {
                if (target == null) {
                    updateFrame(index, NullabilityInterpreter.NULL_VALUE);
                }
                else {
                    updateFrame(index, NullabilityInterpreter.NOT_NULL_VALUE);
                }
                break;
            }
        }

        // 其次，调用父类的方法实现
        super.initJumpTarget(opcode, target);
    }

    private void updateFrame(int index, BasicValue value) {
        int state = getLocalStackState(index);
        if (state == NOT_EXISTED) {
            state = index;
        }

        int length = getMaxLength();
        int local = getLocals();
        int[] localStackStates = getLocalStackStates();
        for (int i = 0; i < length; i++) {
            int currentState = localStackStates[i];
            if ((i == state) || (state == currentState)) {
                if (i < local) {
                    setLocal(i, value);
                }
                else {
                    setStack(i - local, value);
                }
            }
        }
    }

    @Override
    public String toString() {
        List<BasicValue> localList = new ArrayList<>();
        int maxLocals = getLocals();
        for (int i = 0; i < maxLocals; i++) {
            localList.add(getLocal(i));
        }

        List<BasicValue> stackList = new ArrayList<>();
        int maxStack = getStackSize();
        for (int i = 0; i < maxStack; i++) {
            stackList.add(getStack(i));
        }

        String state_str = Arrays.toString(getLocalStackStates());
        String locals_str = list2Str(localList);
        String stack_str = list2Str(stackList);
        return String.format("%s: %s %s %s", state_str, locals_str, SEPARATOR, stack_str);
    }

    private String list2Str(List<BasicValue> list) {
        if (list == null || list.size() == 0) return EMPTY;
        int size = list.size();
        String[] array = new String[size];
        for (int i = 0; i < size - 1; i++) {
            BasicValue item = list.get(i);
            array[i] = item2Str(item);
        }

        {
            // 最后一个值
            int lastIndex = size - 1;
            BasicValue item = list.get(lastIndex);
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

    private String item2Str(BasicValue value) {
        if (value == null) {
            return "null";
        }
        else if (value == BasicValue.UNINITIALIZED_VALUE) {
            return ".";
        }
        else if (value == BasicValue.RETURNADDRESS_VALUE) {
            return "A";
        }
        else if (value == NullabilityInterpreter.UNKNOWN_VALUE) {
            return "unknown";
        }
        else if (value == NullabilityInterpreter.NULL_VALUE) {
            return "null";
        }
        else if (value == NullabilityInterpreter.NOT_NULL_VALUE) {
            return "not-null";
        }
        else if (value == NullabilityInterpreter.NULLABLE_VALUE) {
            return "nullable";
        }
        else {
            return value.getType().getClassName();
        }
    }
}
