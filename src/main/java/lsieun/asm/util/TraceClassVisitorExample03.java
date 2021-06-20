package lsieun.asm.util;

import static org.objectweb.asm.Opcodes.*;

import java.io.PrintWriter;

import org.objectweb.asm.util.TraceClassVisitor;

@SuppressWarnings("Duplicates")
public class TraceClassVisitorExample03 {
    public static void main(String[] args) {
        PrintWriter printWriter = new PrintWriter(System.out);
        TraceClassVisitor cv = new TraceClassVisitor(null, printWriter);
        cv.visit(V1_7, ACC_PUBLIC+ACC_ABSTRACT+ACC_INTERFACE, "sample/HelloWorld",
                null, "java/lang/Object", new String[]{"java/io/Serializable"});
        cv.visitField(ACC_PUBLIC+ACC_FINAL+ACC_STATIC, "LESS", "I",
                null, new Integer(-1)).visitEnd();
        cv.visitField(ACC_PUBLIC+ACC_FINAL+ACC_STATIC, "EQUAL", "I",
                null, new Integer(0)).visitEnd();
        cv.visitField(ACC_PUBLIC+ACC_FINAL+ACC_STATIC, "GREATER", "I",
                null, new Integer(1)).visitEnd();
        cv.visitMethod(ACC_PUBLIC+ACC_ABSTRACT, "compareTo", "(Ljava/lang/Object;)I",
                null, null).visitEnd();
        cv.visitEnd();
    }
}
