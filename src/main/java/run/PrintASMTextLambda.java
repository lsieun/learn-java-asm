package run;

import lsieun.utils.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Print Lambda anonymous inner class.
 *
 * @see ASMPrint
 * @see PrintASMCodeCore
 * @see PrintASMCodeTree
 * @see PrintASMTextClass
 */
public class PrintASMTextLambda {
    public static void main(String[] args) throws IOException {
        // (1) 设置参数
        String str = "[-54, -2, -70, -66, ...]";
        byte[] bytes = StringUtils.array2Bytes(str);
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;

        // (2) 打印结果
        Printer printer = new Textifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(bytes).accept(traceClassVisitor, parsingOptions);
    }
}
