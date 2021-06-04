package run;

import java.lang.reflect.Method;

public class HelloWorldRun {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = Class.forName("sample.HelloWorld");
        Object instance = clazz.newInstance();
        System.out.println(instance);
//        invokeMethod(clazz, "test", instance);
//        invokeMethod(clazz, "printDate", instance);
    }

//    public static void invokeMethod(Class<?> clazz, String methodName, Object instance) throws Exception {
//        Method m = clazz.getDeclaredMethod(methodName);
//        m.invoke(instance);
//    }
}
