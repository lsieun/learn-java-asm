package run;

import lsieun.asm.util.TreePrinter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Print asm tree code.
 *
 * @see ASMPrint
 * @see PrintASMCodeCore
 * @see PrintASMTextClass
 * @see PrintASMTextLambda
 */
public class PrintASMCodeTree {
    public static void main(String[] args) throws IOException {
        // (1) 设置参数
        String className = "sample.HelloWorld";
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;

        // (2) 打印结果
        Printer printer = new TreePrinter();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
    }
}
