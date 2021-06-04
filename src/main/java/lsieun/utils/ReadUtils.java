package lsieun.utils;

import java.io.InputStream;

public class ReadUtils {
    /**
     * @param filepath 例如，/home/user/Workspace/tmp/HelloWorld.class
     * @return
     */
    public static byte[] readByPath(String filepath) {
        System.out.println("Class File Path: file://" + filepath);
        return FileUtils.readBytes(filepath);
    }

    /**
     * @param class_name 例如， lsieun.classfile.MagicNumber
     * @return
     */
    public static byte[] readByProject(String class_name) {
        String filepath = FileUtils.getFilePath(ReadUtils.class, class_name);
        System.out.println("Class File Path: file://" + filepath);

        return FileUtils.readBytes(filepath);
    }

    /**
     * @param clazz 例如 Object.class
     * @return
     */
    public static byte[] readByClassLoader(Class<?> clazz) {
        String class_name = clazz.getName();
        System.out.println("Load Class: " + class_name);
        InputStream in = FileUtils.getInputStream(class_name);
        return FileUtils.readStream(in, true);
    }

    /**
     * @param jar_path 例如，/usr/local/jdk8/jre/lib/rt.jar
     * @param entry_name 例如，java/lang/Object.class
     * @return
     */
    public static byte[] readByJar(String jar_path, String entry_name) {
        String filepath = "jar:file:" + jar_path + "!/" + entry_name;
        System.out.println("Class File Path: " + filepath);

        return JarUtils.readClass(jar_path, entry_name);
    }
}
