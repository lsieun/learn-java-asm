package run;

import lsieun.classfile.InsnRaw;
import lsieun.cst.Const;
import lsieun.utils.HexUtils;

import java.util.List;

public class BytecodeRun {
    public static void main(String[] args) {
        // (1) 准备输入参数
        String input = "    0x0000000: 121a 121c b600 2099 0022 b200 26b2 0026\n" +
                "    0x0000010: b200 2b04 bc0a 5903 044f b600 2fb6 0033\n" +
                "    0x0000020: c000 35b6 0039 a700 22b2 003e b600 42b2\n" +
                "    0x0000030: 0026 b200 2b04 bc0a 5903 044f b600 2fb6\n" +
                "    0x0000040: 0033 c000 35b6 0047 b0";

        // (2) 转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        String[] lines = input.split("\n");
        for (String item : lines) {
            System.out.println(item.trim());
            String[] array = item.split(":");
            sb.append(array[1]);
        }
        String bytecodeHexStr = sb.toString().replaceAll("\\s", "");
        System.out.println(Const.DIVISION_LINE);

        // (3) 打印instruction信息
        byte[] bytes = HexUtils.parse(bytecodeHexStr);
        InsnRaw insnRaw = new InsnRaw(bytes);
        List<String> instructions = insnRaw.getList();
        for (String item : instructions) {
            System.out.println(item);
        }
    }
}
