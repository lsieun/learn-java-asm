package run;

import lsieun.utils.OpcodeConst;

/**
 * Print opcode between {@link #start} and {@link #stop}.
 */
public class PrintOpcodeTable {
    private static int start = 0;
    private static int stop = 256;

    public static void main(String[] args) throws Exception {
        printOpcode();
    }

    public static void printOpcode() {
        if (start < 0) {
            start = 0;
        }
        if (stop > 256) {
            stop = 256;
        }
        int num = stop - start;
        int column = 4;
        int row = num / column;
        int remainder = num % column;
        if (remainder != 0) {
            row++;
        }
        System.out.println("| opcode | mnemonic symbol | opcode | mnemonic symbol | opcode | mnemonic symbol | opcode | mnemonic symbol |");
        System.out.println("|--------|-----------------|--------|-----------------|--------|-----------------|--------|-----------------|");
        for (int i = 0; i < row; i++) {
            int val1 = start + i;
            int val2 = val1 + row;
            int val3 = val2 + row;
            int val4 = val3 + row;
            String line = String.format("| %-6d | %-15s | %-6d | %-15s | %-6d | %-15s | %-6d | %-15s |",
                    val1, getOpcodeName(val1),
                    val2, getOpcodeName(val2),
                    val3, getOpcodeName(val3),
                    val4, getOpcodeName(val4)
            );
            System.out.println(line);
        }
    }

    public static String getOpcodeName(int i) {
        if (i < 0 || i >= OpcodeConst.OPCODE_NAMES_LENGTH) {
            return "";
        }
        if (i < start || i >= stop) {
            return "";
        }
        String opcodeName = OpcodeConst.getOpcodeName(i);
        if (OpcodeConst.ILLEGAL_OPCODE.equals(opcodeName)) {
            opcodeName = "";
        }
        return opcodeName;
    }
}
