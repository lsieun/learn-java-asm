package sample;

import java.util.function.Consumer;

public class HelloWorld {
    public static void main(String[] args) {
        Consumer<String> c = System.out::println;
        c.accept("Hello World");
    }
}