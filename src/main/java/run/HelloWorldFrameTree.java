package run;

import lsieun.asm.analysis.InsnText;
import lsieun.asm.analysis.transition.DestinationInterpreter;
import lsieun.cst.Const;
import lsieun.utils.FileUtils;
import lsieun.utils.FrameUtils;
import lsieun.utils.ValueUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class HelloWorldFrameTree {
    public static void main(String[] args) throws Exception {
        String relative_path = "sample/HelloWorld.class";
        String filepath = FileUtils.getFilePath(relative_path);
        byte[] bytes = FileUtils.readBytes(filepath);

        // (1)构建ClassReader
        ClassReader cr = new ClassReader(bytes);

        // (2) 构建ClassNode
        ClassNode cn = new ClassNode();
        cr.accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);

        // (3) 查看方法Instruction和Frame
        String owner = cn.name;
        List<MethodNode> methods = cn.methods;
        for (MethodNode mn : methods) {
            print(owner, mn, 4);
        }
    }

    private static void print(String owner, MethodNode mn, int option) throws AnalyzerException {
        InsnText insnText = new InsnText();

        switch (option) {
            case 1:
                print(owner, mn, new BasicInterpreter(), null);
                break;
            case 2:
                print(owner, mn, new BasicVerifier(), null);
                break;
            case 3:
                print(owner, mn, new SimpleVerifier(), null);
                break;
            case 4:
                print(owner, mn, new SimpleVerifier(), item -> ValueUtils.fromBasicValue2String(item) + "@" + System.identityHashCode(item));
                break;
            case 5:
                print(owner, mn, new SimpleVerifier(), ValueUtils::fromBasicValue2String);
                break;
            case 6:
                print(owner, mn, new SourceInterpreter(), null);
                break;
            case 7:
                print(owner, mn, new SourceInterpreter(), sourceValue -> insnText.toLines(sourceValue.insns.toArray(new AbstractInsnNode[0])));
                break;
            case 8:
                print(owner, mn, new SourceInterpreter(), sourceValue ->
                        ValueUtils.fromSourceValue2Index(mn, sourceValue)
                );
                break;
            case 9:
                print(owner, mn, new DestinationInterpreter(), sourceValue -> insnText.toLines(sourceValue.insns.toArray(new AbstractInsnNode[0])));
                break;
            case 10:
                print(owner, mn, new DestinationInterpreter(), sourceValue ->
                        ValueUtils.fromSourceValue2Index(mn, sourceValue)
                );
                break;
            default:
                throw new IllegalArgumentException("option is not valid: " + option);
        }
    }

    // NOTE: print方法重点是修改第3个和第4个参数
    public static <V extends Value, T> void print(String owner, MethodNode mn, Interpreter<V> interpreter, Function<V, T> func) throws AnalyzerException {
        System.out.println(mn.name + ":" + mn.desc);

        //（1）获取Instruction信息
        InsnList instructions = mn.instructions;
        int size = instructions.size();
        InsnText insnText = new InsnText();

        //（2）获取Frame信息
        Analyzer<V> analyzer = new Analyzer<>(interpreter);
        Frame<V>[] frames = analyzer.analyze(owner, mn);

        //（3）结合Instruction信息和Frame信息
        // NOTE: 右对齐，使用“%36s”；左对齐，使用“%-36s”。
        String format = "%03d:    %-36s    %s";
        for (int index = 0; index < size; index++) {
            AbstractInsnNode node = instructions.get(index);
            List<String> nodeLines = insnText.toLines(node);

            Frame<V> f = frames[index];
            String frameLine = getFrameLine(f, func);

            String firstLine = String.format(format, index, nodeLines.get(0), frameLine);
            System.out.println(firstLine);
            for (int i = 1; i < nodeLines.size(); i++) {
                String item = nodeLines.get(i);
                String line = String.format("%4s    %-36s", "", item);
                System.out.println(line);
            }
        }

        System.out.println(Const.DIVISION_LINE);
        System.out.println();
    }

    private static <V extends Value, T> String getFrameLine(Frame<V> f, Function<V, T> func) {
        if (f == null) {
            return FrameUtils.toLine(null, null);
        }

        List<Object> localList = new ArrayList<>();
        for (int i = 0; i < f.getLocals(); ++i) {
            V localValue = f.getLocal(i);
            if (func == null) {
                localList.add(localValue);
            }
            else {
                T item = func.apply(localValue);
                localList.add(item);
            }

        }

        List<Object> stackList = new ArrayList<>();
        for (int j = 0; j < f.getStackSize(); ++j) {
            V stackValue = f.getStack(j);
            if (func == null) {
                stackList.add(stackValue);
            }
            else {
                T item = func.apply(stackValue);
                stackList.add(item);
            }
        }

        return FrameUtils.toLine(localList, stackList);
    }
}
