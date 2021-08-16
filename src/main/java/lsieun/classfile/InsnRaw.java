package lsieun.classfile;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class InsnRaw {
    private static final String NO_ARG_FORMAT = "%04d: %-20s";
    private static final String ONE_ARG_FORMAT = "%04d: %-15s %-4s";
    private static final String TWO_ARG_FORMAT = "%04d: %-10s %-4s %-4s";
    private static final String CP_INS_FORMAT = "%04d: %-15s #%-3s";
    private static final String NEW_ARRAY_FORMAT = "%04d: %-10s %-9s";
    private static final String SWITCH_FORMAT = "%04d: %-20s%n";
    private static final String SWITCH_START_FORMAT = "      {%n";
    private static final String CASE_FORMAT = "      %9s: %d%n";
    private static final String SWITCH_STOP_FORMAT = "      }";

    private final byte[] code_bytes;

    public InsnRaw(byte[] code_bytes) {
        this.code_bytes = code_bytes;
    }

    @SuppressWarnings("Duplicates")
    public List<String> getList() {
        List<String> list = new ArrayList<>();

        int length = code_bytes.length;

        boolean wide = false;
        int currentOffSet = 0;

        while (currentOffSet < length) {
            int opcode = readUnsignedByte(currentOffSet);

            final String item;
            final int size;
            switch (opcode) {
                case 0: // nop
                {
                    item = toNoArgIns(currentOffSet, "nop");
                    size = 1;
                    break;
                }
                case 1: // aconst_null
                {
                    item = toNoArgIns(currentOffSet, "aconst_null");
                    size = 1;
                    break;
                }
                case 2: // iconst_m1
                {
                    item = toNoArgIns(currentOffSet, "iconst_m1");
                    size = 1;
                    break;
                }
                case 3: // iconst_0
                {
                    item = toNoArgIns(currentOffSet, "iconst_0");
                    size = 1;
                    break;
                }
                case 4: // iconst_1
                {
                    item = toNoArgIns(currentOffSet, "iconst_1");
                    size = 1;
                    break;
                }
                case 5: // iconst_2
                {
                    item = toNoArgIns(currentOffSet, "iconst_2");
                    size = 1;
                    break;
                }
                case 6: // iconst_3
                {
                    item = toNoArgIns(currentOffSet, "iconst_3");
                    size = 1;
                    break;
                }
                case 7: // iconst_4
                {
                    item = toNoArgIns(currentOffSet, "iconst_4");
                    size = 1;
                    break;
                }
                case 8: // iconst_5
                {
                    item = toNoArgIns(currentOffSet, "iconst_5");
                    size = 1;
                    break;
                }
                case 9: // lconst_0
                {
                    item = toNoArgIns(currentOffSet, "lconst_0");
                    size = 1;
                    break;
                }
                case 10: // lconst_1
                {
                    item = toNoArgIns(currentOffSet, "lconst_1");
                    size = 1;
                    break;
                }
                case 11: // fconst_0
                {
                    item = toNoArgIns(currentOffSet, "fconst_0");
                    size = 1;
                    break;
                }
                case 12: // fconst_1
                {
                    item = toNoArgIns(currentOffSet, "fconst_1");
                    size = 1;
                    break;
                }
                case 13: // fconst_2
                {
                    item = toNoArgIns(currentOffSet, "fconst_2");
                    size = 1;
                    break;
                }
                case 14: // dconst_0
                {
                    item = toNoArgIns(currentOffSet, "dconst_0");
                    size = 1;
                    break;
                }
                case 15: // dconst_1
                {
                    item = toNoArgIns(currentOffSet, "dconst_1");
                    size = 1;
                    break;
                }
                case 16: // bipush
                {
                    int value = readByte(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "bipush", firstArg);
                    size = 2;
                    break;
                }
                case 17: // sipush
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "sipush", firstArg);
                    size = 3;
                    break;
                }
                case 18: // ldc
                {
                    int cpIndex = readUnsignedByte(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "ldc", cpIndex);
                    size = 2;
                    break;
                }
                case 19: // ldc_w
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "ldc_w", cpIndex);
                    size = 3;
                    break;
                }
                case 20: // ldc2_w
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "ldc2_w", cpIndex);
                    size = 3;
                    break;
                }
                case 21: // iload
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "iload", firstArg);
                    break;
                }
                case 22: // lload
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "lload", firstArg);
                    break;
                }
                case 23: // fload
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "fload", firstArg);
                    break;
                }
                case 24: // dload
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "dload", firstArg);
                    break;
                }
                case 25: // aload
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "aload", firstArg);
                    break;
                }
                case 26: // iload_0
                {
                    item = toNoArgIns(currentOffSet, "iload_0");
                    size = 1;
                    break;
                }
                case 27: // iload_1
                {
                    item = toNoArgIns(currentOffSet, "iload_1");
                    size = 1;
                    break;
                }
                case 28: // iload_2
                {
                    item = toNoArgIns(currentOffSet, "iload_2");
                    size = 1;
                    break;
                }
                case 29: // iload_3
                {
                    item = toNoArgIns(currentOffSet, "iload_3");
                    size = 1;
                    break;
                }
                case 30: // lload_0
                {
                    item = toNoArgIns(currentOffSet, "lload_0");
                    size = 1;
                    break;
                }
                case 31: // lload_1
                {
                    item = toNoArgIns(currentOffSet, "lload_1");
                    size = 1;
                    break;
                }
                case 32: // lload_2
                {
                    item = toNoArgIns(currentOffSet, "lload_2");
                    size = 1;
                    break;
                }
                case 33: // lload_3
                {
                    item = toNoArgIns(currentOffSet, "lload_3");
                    size = 1;
                    break;
                }
                case 34: // fload_0
                {
                    item = toNoArgIns(currentOffSet, "fload_0");
                    size = 1;
                    break;
                }
                case 35: // fload_1
                {
                    item = toNoArgIns(currentOffSet, "fload_1");
                    size = 1;
                    break;
                }
                case 36: // fload_2
                {
                    item = toNoArgIns(currentOffSet, "fload_2");
                    size = 1;
                    break;
                }
                case 37: // fload_3
                {
                    item = toNoArgIns(currentOffSet, "fload_3");
                    size = 1;
                    break;
                }
                case 38: // dload_0
                {
                    item = toNoArgIns(currentOffSet, "dload_0");
                    size = 1;
                    break;
                }
                case 39: // dload_1
                {
                    item = toNoArgIns(currentOffSet, "dload_1");
                    size = 1;
                    break;
                }
                case 40: // dload_2
                {
                    item = toNoArgIns(currentOffSet, "dload_2");
                    size = 1;
                    break;
                }
                case 41: // dload_3
                {
                    item = toNoArgIns(currentOffSet, "dload_3");
                    size = 1;
                    break;
                }
                case 42: // aload_0
                {
                    item = toNoArgIns(currentOffSet, "aload_0");
                    size = 1;
                    break;
                }
                case 43: // aload_1
                {
                    item = toNoArgIns(currentOffSet, "aload_1");
                    size = 1;
                    break;
                }
                case 44: // aload_2
                {
                    item = toNoArgIns(currentOffSet, "aload_2");
                    size = 1;
                    break;
                }
                case 45: // aload_3
                {
                    item = toNoArgIns(currentOffSet, "aload_3");
                    size = 1;
                    break;
                }
                case 46: // iaload
                {
                    item = toNoArgIns(currentOffSet, "iaload");
                    size = 1;
                    break;
                }
                case 47: // laload
                {
                    item = toNoArgIns(currentOffSet, "laload");
                    size = 1;
                    break;
                }
                case 48: // faload
                {
                    item = toNoArgIns(currentOffSet, "faload");
                    size = 1;
                    break;
                }
                case 49: // daload
                {
                    item = toNoArgIns(currentOffSet, "daload");
                    size = 1;
                    break;
                }
                case 50: // aaload
                {
                    item = toNoArgIns(currentOffSet, "aaload");
                    size = 1;
                    break;
                }
                case 51: // baload
                {
                    item = toNoArgIns(currentOffSet, "baload");
                    size = 1;
                    break;
                }
                case 52: // caload
                {
                    item = toNoArgIns(currentOffSet, "caload");
                    size = 1;
                    break;
                }
                case 53: // saload
                {
                    item = toNoArgIns(currentOffSet, "saload");
                    size = 1;
                    break;
                }
                case 54: // istore
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "istore", firstArg);
                    break;
                }
                case 55: // lstore
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "lstore", firstArg);
                    break;
                }
                case 56: // fstore
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "fstore", firstArg);
                    break;
                }
                case 57: // dstore
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "dstore", firstArg);
                    break;
                }
                case 58: // astore
                {
                    final int localIndex;
                    if (wide) {
                        localIndex = readUnsignedShort(currentOffSet + 1);
                        wide = false;
                        size = 3;
                    }else {
                        localIndex = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(localIndex);
                    item = toOneArgIns(currentOffSet, "astore", firstArg);
                    break;
                }
                case 59: // istore_0
                {
                    item = toNoArgIns(currentOffSet, "istore_0");
                    size = 1;
                    break;
                }
                case 60: // istore_1
                {
                    item = toNoArgIns(currentOffSet, "istore_1");
                    size = 1;
                    break;
                }
                case 61: // istore_2
                {
                    item = toNoArgIns(currentOffSet, "istore_2");
                    size = 1;
                    break;
                }
                case 62: // istore_3
                {
                    item = toNoArgIns(currentOffSet, "istore_3");
                    size = 1;
                    break;
                }
                case 63: // lstore_0
                {
                    item = toNoArgIns(currentOffSet, "lstore_0");
                    size = 1;
                    break;
                }
                case 64: // lstore_1
                {
                    item = toNoArgIns(currentOffSet, "lstore_1");
                    size = 1;
                    break;
                }
                case 65: // lstore_2
                {
                    item = toNoArgIns(currentOffSet, "lstore_2");
                    size = 1;
                    break;
                }
                case 66: // lstore_3
                {
                    item = toNoArgIns(currentOffSet, "lstore_3");
                    size = 1;
                    break;
                }
                case 67: // fstore_0
                {
                    item = toNoArgIns(currentOffSet, "fstore_0");
                    size = 1;
                    break;
                }
                case 68: // fstore_1
                {
                    item = toNoArgIns(currentOffSet, "fstore_1");
                    size = 1;
                    break;
                }
                case 69: // fstore_2
                {
                    item = toNoArgIns(currentOffSet, "fstore_2");
                    size = 1;
                    break;
                }
                case 70: // fstore_3
                {
                    item = toNoArgIns(currentOffSet, "fstore_3");
                    size = 1;
                    break;
                }
                case 71: // dstore_0
                {
                    item = toNoArgIns(currentOffSet, "dstore_0");
                    size = 1;
                    break;
                }
                case 72: // dstore_1
                {
                    item = toNoArgIns(currentOffSet, "dstore_1");
                    size = 1;
                    break;
                }
                case 73: // dstore_2
                {
                    item = toNoArgIns(currentOffSet, "dstore_2");
                    size = 1;
                    break;
                }
                case 74: // dstore_3
                {
                    item = toNoArgIns(currentOffSet, "dstore_3");
                    size = 1;
                    break;
                }
                case 75: // astore_0
                {
                    item = toNoArgIns(currentOffSet, "astore_0");
                    size = 1;
                    break;
                }
                case 76: // astore_1
                {
                    item = toNoArgIns(currentOffSet, "astore_1");
                    size = 1;
                    break;
                }
                case 77: // astore_2
                {
                    item = toNoArgIns(currentOffSet, "astore_2");
                    size = 1;
                    break;
                }
                case 78: // astore_3
                {
                    item = toNoArgIns(currentOffSet, "astore_3");
                    size = 1;
                    break;
                }
                case 79: // iastore
                {
                    item = toNoArgIns(currentOffSet, "iastore");
                    size = 1;
                    break;
                }
                case 80: // lastore
                {
                    item = toNoArgIns(currentOffSet, "lastore");
                    size = 1;
                    break;
                }
                case 81: // fastore
                {
                    item = toNoArgIns(currentOffSet, "fastore");
                    size = 1;
                    break;
                }
                case 82: // dastore
                {
                    item = toNoArgIns(currentOffSet, "dastore");
                    size = 1;
                    break;
                }
                case 83: // aastore
                {
                    item = toNoArgIns(currentOffSet, "aastore");
                    size = 1;
                    break;
                }
                case 84: // bastore
                {
                    item = toNoArgIns(currentOffSet, "bastore");
                    size = 1;
                    break;
                }
                case 85: // castore
                {
                    item = toNoArgIns(currentOffSet, "castore");
                    size = 1;
                    break;
                }
                case 86: // sastore
                {
                    item = toNoArgIns(currentOffSet, "sastore");
                    size = 1;
                    break;
                }
                case 87: // pop
                {
                    item = toNoArgIns(currentOffSet, "pop");
                    size = 1;
                    break;
                }
                case 88: // pop2
                {
                    item = toNoArgIns(currentOffSet, "pop2");
                    size = 1;
                    break;
                }
                case 89: // dup
                {
                    item = toNoArgIns(currentOffSet, "dup");
                    size = 1;
                    break;
                }
                case 90: // dup_x1
                {
                    item = toNoArgIns(currentOffSet, "dup_x1");
                    size = 1;
                    break;
                }
                case 91: // dup_x2
                {
                    item = toNoArgIns(currentOffSet, "dup_x2");
                    size = 1;
                    break;
                }
                case 92: // dup2
                {
                    item = toNoArgIns(currentOffSet, "dup2");
                    size = 1;
                    break;
                }
                case 93: // dup2_x1
                {
                    item = toNoArgIns(currentOffSet, "dup2_x1");
                    size = 1;
                    break;
                }
                case 94: // dup2_x2
                {
                    item = toNoArgIns(currentOffSet, "dup2_x2");
                    size = 1;
                    break;
                }
                case 95: // swap
                {
                    item = toNoArgIns(currentOffSet, "swap");
                    size = 1;
                    break;
                }
                case 96: // iadd
                {
                    item = toNoArgIns(currentOffSet, "iadd");
                    size = 1;
                    break;
                }
                case 97: // ladd
                {
                    item = toNoArgIns(currentOffSet, "ladd");
                    size = 1;
                    break;
                }
                case 98: // fadd
                {
                    item = toNoArgIns(currentOffSet, "fadd");
                    size = 1;
                    break;
                }
                case 99: // dadd
                {
                    item = toNoArgIns(currentOffSet, "dadd");
                    size = 1;
                    break;
                }
                case 100: // isub
                {
                    item = toNoArgIns(currentOffSet, "isub");
                    size = 1;
                    break;
                }
                case 101: // lsub
                {
                    item = toNoArgIns(currentOffSet, "lsub");
                    size = 1;
                    break;
                }
                case 102: // fsub
                {
                    item = toNoArgIns(currentOffSet, "fsub");
                    size = 1;
                    break;
                }
                case 103: // dsub
                {
                    item = toNoArgIns(currentOffSet, "dsub");
                    size = 1;
                    break;
                }
                case 104: // imul
                {
                    item = toNoArgIns(currentOffSet, "imul");
                    size = 1;
                    break;
                }
                case 105: // lmul
                {
                    item = toNoArgIns(currentOffSet, "lmul");
                    size = 1;
                    break;
                }
                case 106: // fmul
                {
                    item = toNoArgIns(currentOffSet, "fmul");
                    size = 1;
                    break;
                }
                case 107: // dmul
                {
                    item = toNoArgIns(currentOffSet, "dmul");
                    size = 1;
                    break;
                }
                case 108: // idiv
                {
                    item = toNoArgIns(currentOffSet, "idiv");
                    size = 1;
                    break;
                }
                case 109: // ldiv
                {
                    item = toNoArgIns(currentOffSet, "ldiv");
                    size = 1;
                    break;
                }
                case 110: // fdiv
                {
                    item = toNoArgIns(currentOffSet, "fdiv");
                    size = 1;
                    break;
                }
                case 111: // ddiv
                {
                    item = toNoArgIns(currentOffSet, "ddiv");
                    size = 1;
                    break;
                }
                case 112: // irem
                {
                    item = toNoArgIns(currentOffSet, "irem");
                    size = 1;
                    break;
                }
                case 113: // lrem
                {
                    item = toNoArgIns(currentOffSet, "lrem");
                    size = 1;
                    break;
                }
                case 114: // frem
                {
                    item = toNoArgIns(currentOffSet, "frem");
                    size = 1;
                    break;
                }
                case 115: // drem
                {
                    item = toNoArgIns(currentOffSet, "drem");
                    size = 1;
                    break;
                }
                case 116: // ineg
                {
                    item = toNoArgIns(currentOffSet, "ineg");
                    size = 1;
                    break;
                }

                case 117: // lneg
                {
                    item = toNoArgIns(currentOffSet, "lneg");
                    size = 1;
                    break;
                }

                case 118: // fneg
                {
                    item = toNoArgIns(currentOffSet, "fneg");
                    size = 1;
                    break;
                }

                case 119: // dneg
                {
                    item = toNoArgIns(currentOffSet, "dneg");
                    size = 1;
                    break;
                }

                case 120: // ishl
                {
                    item = toNoArgIns(currentOffSet, "ishl");
                    size = 1;
                    break;
                }

                case 121: // lshl
                {
                    item = toNoArgIns(currentOffSet, "lshl");
                    size = 1;
                    break;
                }

                case 122: // ishr
                {
                    item = toNoArgIns(currentOffSet, "ishr");
                    size = 1;
                    break;
                }

                case 123: // lshr
                {
                    item = toNoArgIns(currentOffSet, "lshr");
                    size = 1;
                    break;
                }

                case 124: // iushr
                {
                    item = toNoArgIns(currentOffSet, "iushr");
                    size = 1;
                    break;
                }

                case 125: // lushr
                {
                    item = toNoArgIns(currentOffSet, "lushr");
                    size = 1;
                    break;
                }

                case 126: // iand
                {
                    item = toNoArgIns(currentOffSet, "iand");
                    size = 1;
                    break;
                }

                case 127: // land
                {
                    item = toNoArgIns(currentOffSet, "land");
                    size = 1;
                    break;
                }

                case 128: // ior
                {
                    item = toNoArgIns(currentOffSet, "ior");
                    size = 1;
                    break;
                }

                case 129: // lor
                {
                    item = toNoArgIns(currentOffSet, "lor");
                    size = 1;
                    break;
                }

                case 130: // ixor
                {
                    item = toNoArgIns(currentOffSet, "ixor");
                    size = 1;
                    break;
                }

                case 131: // lxor
                {
                    item = toNoArgIns(currentOffSet, "lxor");
                    size = 1;
                    break;
                }
                case 132: // iinc
                {
                    int index;
                    int val;
                    if (wide) {
                        index = readUnsignedShort(currentOffSet + 1);
                        val = readShort(currentOffSet + 3);
                        size = 5;
                    }
                    else {
                        index = readUnsignedByte(currentOffSet + 1);
                        val = readByte(currentOffSet + 2);
                        size = 3;
                    }
                    item = toTwoArgIns(currentOffSet, "iinc", String.valueOf(index), String.valueOf(val));
                    break;
                }

                case 133: // i2l
                {
                    item = toNoArgIns(currentOffSet, "i2l");
                    size = 1;
                    break;
                }

                case 134: // i2f
                {
                    item = toNoArgIns(currentOffSet, "i2f");
                    size = 1;
                    break;
                }

                case 135: // i2d
                {
                    item = toNoArgIns(currentOffSet, "i2d");
                    size = 1;
                    break;
                }

                case 136: // l2i
                {
                    item = toNoArgIns(currentOffSet, "l2i");
                    size = 1;
                    break;
                }

                case 137: // l2f
                {
                    item = toNoArgIns(currentOffSet, "l2f");
                    size = 1;
                    break;
                }

                case 138: // l2d
                {
                    item = toNoArgIns(currentOffSet, "l2d");
                    size = 1;
                    break;
                }

                case 139: // f2i
                {
                    item = toNoArgIns(currentOffSet, "f2i");
                    size = 1;
                    break;
                }

                case 140: // f2l
                {
                    item = toNoArgIns(currentOffSet, "f2l");
                    size = 1;
                    break;
                }

                case 141: // f2d
                {
                    item = toNoArgIns(currentOffSet, "f2d");
                    size = 1;
                    break;
                }

                case 142: // d2i
                {
                    item = toNoArgIns(currentOffSet, "d2i");
                    size = 1;
                    break;
                }

                case 143: // d2l
                {
                    item = toNoArgIns(currentOffSet, "d2l");
                    size = 1;
                    break;
                }

                case 144: // d2f
                {
                    item = toNoArgIns(currentOffSet, "d2f");
                    size = 1;
                    break;
                }

                case 145: // i2b
                {
                    item = toNoArgIns(currentOffSet, "i2b");
                    size = 1;
                    break;
                }

                case 146: // i2c
                {
                    item = toNoArgIns(currentOffSet, "i2c");
                    size = 1;
                    break;
                }

                case 147: // i2s
                {
                    item = toNoArgIns(currentOffSet, "i2s");
                    size = 1;
                    break;
                }
                case 148: // lcmp
                {
                    item = toNoArgIns(currentOffSet, "lcmp");
                    size = 1;
                    break;
                }

                case 149: // fcmpl
                {
                    item = toNoArgIns(currentOffSet, "fcmpl");
                    size = 1;
                    break;
                }

                case 150: // fcmpg
                {
                    item = toNoArgIns(currentOffSet, "fcmpg");
                    size = 1;
                    break;
                }

                case 151: // dcmpl
                {
                    item = toNoArgIns(currentOffSet, "dcmpl");
                    size = 1;
                    break;
                }

                case 152: // dcmpg
                {
                    item = toNoArgIns(currentOffSet, "dcmpg");
                    size = 1;
                    break;
                }
                case 153: // ifeq
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "ifeq", firstArg);
                    size = 3;
                    break;
                }

                case 154: // ifne
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "ifne", firstArg);
                    size = 3;
                    break;
                }

                case 155: // iflt
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "iflt", firstArg);
                    size = 3;
                    break;
                }

                case 156: // ifge
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "ifge", firstArg);
                    size = 3;
                    break;
                }

                case 157: // ifgt
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "ifgt", firstArg);
                    size = 3;
                    break;
                }

                case 158: // ifle
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "ifle", firstArg);
                    size = 3;
                    break;
                }

                case 159: // if_icmpeq
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_icmpeq", firstArg);
                    size = 3;
                    break;
                }

                case 160: // if_icmpne
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_icmpne", firstArg);
                    size = 3;
                    break;
                }

                case 161: // if_icmplt
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_icmplt", firstArg);
                    size = 3;
                    break;
                }

                case 162: // if_icmpge
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_icmpge", firstArg);
                    size = 3;
                    break;
                }

                case 163: // if_icmpgt
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_icmpgt", firstArg);
                    size = 3;
                    break;
                }

                case 164: // if_icmple
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_icmple", firstArg);
                    size = 3;
                    break;
                }

                case 165: // if_acmpeq
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_acmpeq", firstArg);
                    size = 3;
                    break;
                }

                case 166: // if_acmpne
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "if_acmpne", firstArg);
                    size = 3;
                    break;
                }

                case 167: // goto
                {
                    int value = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(value);
                    item = toOneArgIns(currentOffSet, "goto", firstArg);
                    size = 3;
                    break;
                }

                case 168: // jsr
                {
                    int address = readShort(currentOffSet + 1);
                    String firstArg = String.valueOf(address);
                    item = toOneArgIns(currentOffSet, "jsr", firstArg);
                    size = 3;
                    break;
                }
                case 169: // ret
                {
                    int index;
                    if (wide) {
                        index = readUnsignedShort(currentOffSet + 1);
                        size = 3;
                        wide = false;
                    }
                    else {
                        index = readUnsignedByte(currentOffSet + 1);
                        size = 2;
                    }
                    String firstArg = String.valueOf(index);
                    item = toOneArgIns(currentOffSet, "ret", firstArg);

                    break;
                }
                case 170: // tableswitch
                {
                    int pad = 3 - currentOffSet % 4;
                    int defaultOffset = readInt(currentOffSet + pad + 1);
                    int low = readInt(currentOffSet + pad + 1 + 4);
                    int high = readInt(currentOffSet + pad + 1 + 8);

                    StringBuilder sb = new StringBuilder();
                    Formatter fm = new Formatter(sb);
                    fm.format(SWITCH_FORMAT, currentOffSet, "tableswitch");
                    fm.format(SWITCH_START_FORMAT);
                    int caseCount = high - low + 1;
                    for (int i = 0; i < caseCount; i++) {
                        String caseValue = String.valueOf(low + i);
                        int offset = readInt(currentOffSet + pad + 1 + 12 + i * 4);
                        fm.format(CASE_FORMAT, caseValue, offset);
                    }
                    fm.format(CASE_FORMAT, "default", defaultOffset);
                    fm.format(SWITCH_STOP_FORMAT);

                    item = sb.toString();
                    size = 1 /*opcode*/ + pad + 12/*default-low-high*/ + (high - low + 1) * 4;
                    break;
                }
                case 171: // lookupswitch
                {
                    int pad = 3 - currentOffSet % 4;
                    int defaultOffset = readInt(currentOffSet + pad + 1);
                    int npairs = readInt(currentOffSet + pad + 1 + 4);

                    StringBuilder sb = new StringBuilder();
                    Formatter fm = new Formatter(sb);
                    fm.format(SWITCH_FORMAT, currentOffSet, "lookupswitch");
                    fm.format(SWITCH_START_FORMAT);
                    for (int i = 0; i < npairs; i++) {
                        int caseConst = readInt(currentOffSet + pad + 1 + 8 + i * 8);
                        String caseValue = String.valueOf(caseConst);
                        int offset = readInt(currentOffSet + pad + 1 + 8 + i * 8 + 4);
                        fm.format(CASE_FORMAT, caseValue, offset);
                    }
                    fm.format(CASE_FORMAT, "default", defaultOffset);
                    fm.format(SWITCH_STOP_FORMAT);

                    item = sb.toString();
                    size = 1 /*opcode*/ + pad + 8/*default-npairs*/ + (npairs) * 8;
                    break;
                }
                case 172: // ireturn
                {
                    item = toNoArgIns(currentOffSet, "ireturn");
                    size = 1;
                    break;
                }
                case 173: // lreturn
                {
                    item = toNoArgIns(currentOffSet, "lreturn");
                    size = 1;
                    break;
                }
                case 174: // freturn
                {
                    item = toNoArgIns(currentOffSet, "freturn");
                    size = 1;
                    break;
                }
                case 175: // dreturn
                {
                    item = toNoArgIns(currentOffSet, "dreturn");
                    size = 1;
                    break;
                }
                case 176: // areturn
                {
                    item = toNoArgIns(currentOffSet, "areturn");
                    size = 1;
                    break;
                }
                case 177: // return
                {
                    item = toNoArgIns(currentOffSet, "return");
                    size = 1;
                    break;
                }
                case 178: // getstatic
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "getstatic", cpIndex);
                    size = 3;
                    break;
                }
                case 179: // putstatic
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "putstatic", cpIndex);
                    size = 3;
                    break;
                }
                case 180: // getfield
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "getfield", cpIndex);
                    size = 3;
                    break;
                }
                case 181: // putfield
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "putfield", cpIndex);
                    size = 3;
                    break;
                }
                case 182: // invokevirtual
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "invokevirtual", cpIndex);
                    size = 3;
                    break;
                }
                case 183: // invokespecial
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "invokespecial", cpIndex);
                    size = 3;
                    break;
                }
                case 184: // invokestatic
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "invokestatic", cpIndex);
                    size = 3;
                    break;
                }
                case 185: // invokeinterface
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    int count = readUnsignedByte(currentOffSet + 3);
                    String firstArg = String.format("#%d  %d", cpIndex, count);
                    item = toOneArgIns(currentOffSet, "invokeinterface", firstArg);
                    size = 5;
                    break;
                }
                case 186: // invokedynamic
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    String firstArg = String.format("#%d", cpIndex);
                    item = toOneArgIns(currentOffSet, "invokedynamic", firstArg);
                    size = 5;
                    break;
                }
                case 187: // new
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "new", cpIndex);
                    size = 3;
                    break;
                }
                case 188: // newarray
                {
                    int atype = readByte(currentOffSet + 1);
                    final String firstArg;
                    switch (atype) {
                        case 4: {
                            firstArg = "4 (boolean)";
                            break;
                        }
                        case 5: {
                            firstArg = "5 (char)";
                            break;
                        }
                        case 6: {
                            firstArg = "6 (float)";
                            break;
                        }
                        case 7: {
                            firstArg = "7 (double)";
                            break;
                        }
                        case 8: {
                            firstArg = "8 (byte)";
                            break;
                        }
                        case 9: {
                            firstArg = "9 (short)";
                            break;
                        }
                        case 10: {
                            firstArg = "10 (int)";
                            break;
                        }
                        case 11: {
                            firstArg = "11 (long)";
                            break;
                        }
                        default:
                            throw new RuntimeException("atype is not supported: " + atype);
                    }
                    item = String.format(NEW_ARRAY_FORMAT, currentOffSet, "newarray", firstArg);
                    size = 2;
                    break;
                }
                case 189: // anewarray
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "anewarray", cpIndex);
                    size = 3;
                    break;
                }
                case 190: // arraylength
                {
                    item = toNoArgIns(currentOffSet, "arraylength");
                    size = 1;
                    break;
                }
                case 191: // athrow
                {
                    item = toNoArgIns(currentOffSet, "athrow");
                    size = 1;
                    break;
                }
                case 192: // checkcast
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "checkcast", cpIndex);
                    size = 3;
                    break;
                }
                case 193: // instanceof
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    item = visitCPIns(currentOffSet, "instanceof", cpIndex);
                    size = 3;
                    break;
                }
                case 194: // monitorenter
                {
                    item = toNoArgIns(currentOffSet, "monitorenter");
                    size = 1;
                    break;
                }
                case 195: // monitorexit
                {
                    item = toNoArgIns(currentOffSet, "monitorexit");
                    size = 1;
                    break;
                }
                case 196: // wide
                {
                    wide = true;
                    item = toNoArgIns(currentOffSet, "wide");
                    size = 1;
                    break;
                }
                case 197: // multianewarray
                {
                    int cpIndex = readUnsignedShort(currentOffSet + 1);
                    int dimensions = readByte(currentOffSet + 3);

                    String firstArg = String.format("#%d  %d", cpIndex, dimensions);
                    item = toOneArgIns(currentOffSet, "multianewarray", firstArg);
                    size = 4;
                    break;
                }
                case 198: // ifnull
                {
                    int offset = readUnsignedShort(currentOffSet + 1);
                    String firstArg = String.valueOf(offset);
                    item = toOneArgIns(currentOffSet, "ifnull", firstArg);
                    size = 3;
                    break;
                }
                case 199: // ifnonnull
                {
                    int offset = readUnsignedShort(currentOffSet + 1);
                    String firstArg = String.valueOf(offset);
                    item = toOneArgIns(currentOffSet, "ifnonnull", firstArg);
                    size = 3;
                    break;
                }
                case 200: // goto_w
                {
                    int offset = readInt(currentOffSet + 1);
                    String firstArg = String.valueOf(offset);
                    item = toOneArgIns(currentOffSet, "goto_w", firstArg);
                    size = 5;
                    break;
                }
                case 201: // jsr_w
                {
                    int offset = readInt(currentOffSet + 1);
                    String firstArg = String.valueOf(offset);
                    item = toOneArgIns(currentOffSet, "jsr_w", firstArg);
                    size = 5;
                    break;
                }
                default:
                    throw new RuntimeException("illegal opcode: " + opcode);
            }
            list.add(item);
            currentOffSet += size;
        }

        return list;
    }

    private String toNoArgIns(int offset, String mnemonic_symbol) {
        return String.format(NO_ARG_FORMAT, offset, mnemonic_symbol);
    }

    private String toOneArgIns(int currentOffSet, String mnemonic_symbol, String firstArg) {
        return String.format(ONE_ARG_FORMAT, currentOffSet, mnemonic_symbol, firstArg);
    }

    private String toTwoArgIns(int currentOffSet, String mnemonic_symbol, String firstArg, String secondArg) {
        return String.format(TWO_ARG_FORMAT, currentOffSet, mnemonic_symbol, firstArg, secondArg);
    }

    private String visitCPIns(int currentOffSet, String mnemonic_symbol, int cpIndex) {
        return String.format(CP_INS_FORMAT, currentOffSet, mnemonic_symbol, cpIndex);
    }

    public int readByte(final int offset) {
        return code_bytes[offset];
    }

    public int readUnsignedByte(final int offset) {
        return code_bytes[offset] & 0xFF;
    }

    public short readShort(final int offset) {
        byte[] classBuffer = code_bytes;
        return (short) (((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF));
    }

    public int readUnsignedShort(final int offset) {
        byte[] classBuffer = code_bytes;
        return ((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF);
    }

    public int readInt(final int offset) {
        byte[] classBuffer = code_bytes;
        return ((classBuffer[offset] & 0xFF) << 24)
                | ((classBuffer[offset + 1] & 0xFF) << 16)
                | ((classBuffer[offset + 2] & 0xFF) << 8)
                | (classBuffer[offset + 3] & 0xFF);
    }

    public long readLong(final int offset) {
        long l1 = readInt(offset);
        long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
        return (l1 << 32) | l0;
    }
}
