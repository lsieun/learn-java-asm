package run.jar;

import lsieun.utils.FileUtils;
import lsieun.utils.JarUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

public class CompareTwoJarFile {
    public static void main(String[] args) {
        String jarPath1 = "D:/git-repo/learn-java-asm/first.jar";
        String jarPath2 = "D:/git-repo/learn-java-asm/second.jar";

        List<String> list1 = JarUtils.getAllEntries(jarPath1);
        List<String> list2 = JarUtils.getAllEntries(jarPath2);
        Set<String> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);
        String message = String.format("total: %s, list1=%s, list2=%s", set.size(), list1.size(), list2.size());
        System.out.println(message);

        Map<String, ByteArrayOutputStream> map1 = JarUtils.getAllEntryMap(jarPath1, list1);
        Map<String, ByteArrayOutputStream> map2 = JarUtils.getAllEntryMap(jarPath2, list2);

        for (String item : set) {
            // 第一种情况，路径，不处理
            if (item.endsWith("/")) continue;

            byte[] bytes1 = null;
            byte[] bytes2 = null;

            ByteArrayOutputStream bao1 = map1.get(item);
            ByteArrayOutputStream bao2 = map2.get(item);

            if (bao1 != null) {
                bytes1 = bao1.toByteArray();
            }
            if (bao2 != null) {
                bytes2 = bao2.toByteArray();
            }

            // 第二种情况，相等，不处理
            boolean equals = Arrays.equals(bytes1, bytes2);
            if (equals) continue;

            // 第三种情况，不相等，进行输出
            if (!item.endsWith(".class")) {
                generateOtherFile(bytes1, item, "a");
                generateOtherFile(bytes2, item, "b");
            }
            else {
                generateClassText(bytes1, "a");
                generateClassText(bytes2, "b");
            }
        }
    }

    public static void generateOtherFile(byte[] bytes, String item, String middle) {
        if (bytes == null || bytes.length == 0) return;

        int lastIndex = item.lastIndexOf(".");

        String prefix;
        String suffix;
        if (lastIndex != -1) {
            prefix = item.substring(0, lastIndex);
            suffix = item.substring(lastIndex);
        }
        else {
            prefix = item;
            suffix = "";
        }

        String newItem = prefix + middle + suffix;
        String filepath = FileUtils.getFilePath(newItem);
        FileUtils.writeBytes(filepath, bytes);
    }

    public static void generateClassText(byte[] bytes, String suffix) {
        if (bytes == null || bytes.length == 0) return;

        // 第一步，创建ClassReader
        ClassReader cr = new ClassReader(bytes);
        String className = cr.getClassName();
        String filepath = FileUtils.getFilePath(className + "-" + suffix + ".txt");

        // 第二步，创建ClassVisitor
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        Printer printer = new Textifier();
        PrintWriter printWriter = new PrintWriter(bao, true);
        ClassVisitor cv = new TraceClassVisitor(null, printer, printWriter);

        // 第三步，连接ClassReader和ClassVisitor
        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        cr.accept(cv, parsingOptions);

        // 第四步，输出到文件
        FileUtils.writeBytes(filepath, bao.toByteArray());
    }
}
