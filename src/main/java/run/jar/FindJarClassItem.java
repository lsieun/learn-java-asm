package run.jar;

import lsieun.utils.JarUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class FindJarClassItem {
    public static void main(String[] args) {
        String jarPath = "D:/git-repo/learn-java-asm/someJar.jar";
        List<String> list = JarUtils.getClassEntries(jarPath);
        Map<String, ByteArrayOutputStream> map = JarUtils.getAllEntryMap(jarPath, list);
        for (Map.Entry<String, ByteArrayOutputStream> entry : map.entrySet()) {
            String item = entry.getKey();
            ByteArrayOutputStream bao = entry.getValue();
            byte[] bytes = bao.toByteArray();

            ClassReader cr = new ClassReader(bytes);
            ClassNode cn = new ClassNode();
            cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

            List<MethodNode> methods = cn.methods;
            for (MethodNode mn : methods) {
                String methodName = mn.name;
                String methodDesc = mn.desc;
                if (methodDesc.endsWith("Ljava/security/PublicKey;")) {
                    System.out.println(item + "." + methodName + ":" + methodDesc);
                }
            }
        }
    }
}
