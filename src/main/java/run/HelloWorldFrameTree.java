package run;

import lsieun.asm.analysis.InsnText;
import lsieun.asm.analysis.nullability.*;
import lsieun.asm.analysis.transition.DestinationInterpreter;
import lsieun.utils.FileUtils;
import lsieun.utils.FrameUtils;
import lsieun.utils.ValueUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.*;

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
            print(owner, mn, 5);
        }
    }

    private static void print(String owner, MethodNode mn, int option) throws AnalyzerException {
        InsnText insnText = new InsnText();

        switch (option) {
            case 0: {
                Analyzer<BasicValue> analyzer = new Analyzer<>(new SimpleVerifier());
                FrameUtils.printGraph(owner, mn, analyzer, ValueUtils::fromBasicValue2String);
                break;
            }
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
            case 11:
                print(owner, mn, new NullDeferenceInterpreter(Opcodes.ASM9), value -> {
                    if (value.isReference()) {
                        if (value == NullDeferenceInterpreter.NULL_VALUE) {
                            return "null";
                        }
                        else if (value == NullDeferenceInterpreter.MAYBE_NULL_VALUE) {
                            return "may-be-null";
                        }
                    }

                    return value.toString();
                });
                break;
            case 12: {
                Analyzer<NullabilityValue> analyzer = new NullabilityAnalyzer(new NullabilityInterpreter(Opcodes.ASM9));
                FrameUtils.printFrames(owner, mn, analyzer, ValueUtils::fromNullabilityValue2String);
                break;
            }
            default:
                throw new IllegalArgumentException("option is not valid: " + option);
        }
    }

    // NOTE: print方法重点是修改第3个和第4个参数
    public static <V extends Value, T> void print(String owner, MethodNode mn, Interpreter<V> interpreter, Function<V, T> func) throws AnalyzerException {
        Analyzer<V> analyzer = new Analyzer<>(interpreter);
        FrameUtils.printFrames(owner, mn, analyzer, func);
    }
}
