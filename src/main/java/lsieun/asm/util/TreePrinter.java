package lsieun.asm.util;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;

import java.util.Collections;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class TreePrinter extends Printer {
    private static final int ACCESS_CLASS = 0x40000;
    private static final int ACCESS_FIELD = 0x80000;
    private static final int ACCESS_INNER = 0x100000;
    private static final int ACCESS_MODULE = 0x200000;

    private static final String COMMA = "\", \"";
    private static final String NEW_OBJECT_ARRAY = ", new Object[] {";
    private static final String END_ARRAY = " }";

    private static final Map<Integer, String> CLASS_VERSIONS;

    static {
        HashMap<Integer, String> classVersions = new HashMap<>();
        classVersions.put(Opcodes.V1_1, "V1_1");
        classVersions.put(Opcodes.V1_2, "V1_2");
        classVersions.put(Opcodes.V1_3, "V1_3");
        classVersions.put(Opcodes.V1_4, "V1_4");
        classVersions.put(Opcodes.V1_5, "V1_5");
        classVersions.put(Opcodes.V1_6, "V1_6");
        classVersions.put(Opcodes.V1_7, "V1_7");
        classVersions.put(Opcodes.V1_8, "V1_8");
        classVersions.put(Opcodes.V9, "V9");
        classVersions.put(Opcodes.V10, "V10");
        classVersions.put(Opcodes.V11, "V11");
        classVersions.put(Opcodes.V12, "V12");
        classVersions.put(Opcodes.V13, "V13");
        classVersions.put(Opcodes.V14, "V14");
        classVersions.put(Opcodes.V15, "V15");
        classVersions.put(Opcodes.V16, "V16");
        CLASS_VERSIONS = Collections.unmodifiableMap(classVersions);
    }

    protected final String name;

    protected Map<Label, String> labelNames;

    public TreePrinter() {
        this(Opcodes.ASM9, "classNode");
    }

    public TreePrinter(int api, String visitorVariableName) {
        super(api);
        this.name = visitorVariableName;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String simpleName;
        if (name == null) {
            simpleName = "module-info";
        }
        else {
            int lastSlashIndex = name.lastIndexOf('/');
            if (lastSlashIndex == -1) {
                simpleName = name;
            }
            else {
                text.add("package asm." + name.substring(0, lastSlashIndex).replace('/', '.') + ";\n");
                simpleName = name.substring(lastSlashIndex + 1).replaceAll("[-\\(\\)]", "_");
            }
        }

        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        String versionString = CLASS_VERSIONS.get(version);
        fm.format("import org.objectweb.asm.*;%n");
        fm.format("import org.objectweb.asm.tree.*;%n%n");
        fm.format("public class %sDump implements Opcodes {%n%n", simpleName);
        fm.format("public static byte[] dump () throws Exception {%n");
        fm.format("    ClassNode cn = new ClassNode();%n");
        fm.format("    %s = %s;%n", "cn.version", versionString);
        fm.format("    %s = %s;%n", "cn.access", toAccessFlags(access | ACCESS_CLASS));
        fm.format("    %s = %s;%n", "cn.name", toConstant(name));
        fm.format("    %s = %s;%n", "cn.signature", toConstant(signature));
        fm.format("    %s = %s;%n", "cn.superName", toConstant(superName));
        if (interfaces != null) {
            for (String item : interfaces) {
                fm.format("    cn.interfaces.add(%s);%n", toConstant(item));
            }
        }
        fm.format("%n");

        text.add(stringBuilder.toString());
    }

    @Override
    public void visitSource(String source, String debug) {
        // do nothing
    }

    @Override
    public void visitOuterClass(String owner, String name, String descriptor) {
        // do nothing
    }

    @Override
    public TreePrinter visitClassAnnotation(String descriptor, boolean visible) {
        return null;
    }

    @Override
    public void visitClassAttribute(Attribute attribute) {
        // do nothing
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        // do nothing
    }

    @Override
    public TreePrinter visitField(int access, String name, String descriptor, String signature, Object value) {
        text.add("    {" + System.lineSeparator());

        String accessFlags = toAccessFlags(access | ACCESS_FIELD);

        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        FieldNode fieldNode = new FieldNode(%s, %s, %s, %s, %s);%n", accessFlags, toConstant(name), toConstant(descriptor), toConstant(signature), toConstant(value));
        fm.format("        cn.fields.add(fieldNode);%n");
        text.add(stringBuilder.toString());

        TreePrinter printer = createVariable("fieldNode");
        text.add(printer.getText());
        text.add("    }" + System.lineSeparator() + System.lineSeparator());
        return printer;
    }

    @Override
    public TreePrinter visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        text.add("    {" + System.lineSeparator());

        String exceptionString = "null";
        if (exceptions != null && exceptions.length > 0) {
            exceptionString = "new String[] {";
            for (int i = 0; i < exceptions.length; ++i) {
                exceptionString += (i == 0 ? " " : ", ");
                exceptionString += toConstant(exceptions[i]);
            }
            exceptionString += " }";
        }
        String accessFlags = toAccessFlags(access);
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        MethodNode methodNode = new MethodNode(%s, %s, %s, %s, %s);%n", accessFlags, toConstant(name), toConstant(descriptor), toConstant(signature), exceptionString);
        fm.format("        cn.methods.add(methodNode);%n");

        boolean isAbstractMethod = (access & Opcodes.ACC_ABSTRACT) != 0;
        if (!isAbstractMethod) {
            fm.format("%n");
            fm.format("        InsnList il = methodNode.instructions;%n");
        }
        text.add(stringBuilder.toString());

        TreePrinter printer = createVariable("methodNode");
        text.add(printer.getText());

        text.add("    }" + System.lineSeparator() + System.lineSeparator());
        return printer;
    }

    @Override
    public void visitClassEnd() {
        // do nothing
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);%n");
        fm.format("    cn.accept(cw);%n");
        fm.format("    return cw.toByteArray();%n");

        text.add(stringBuilder.toString());

        text.add("}" + System.lineSeparator());
        text.add("}");
    }

    @Override
    public void visit(String name, Object value) {
        // do nothing
    }

    @Override
    public void visitEnum(String name, String descriptor, String value) {
        // do nothing
    }

    @Override
    public TreePrinter visitAnnotation(String name, String descriptor) {
        return null;
    }

    @Override
    public TreePrinter visitArray(String name) {
        return null;
    }

    @Override
    public void visitAnnotationEnd() {
        // do nothing
    }

    @Override
    public TreePrinter visitFieldAnnotation(String descriptor, boolean visible) {
        return null;
    }

    @Override
    public void visitFieldAttribute(Attribute attribute) {
        // do nothing
    }

    @Override
    public void visitFieldEnd() {
        // do nothing
    }

    @Override
    public TreePrinter visitAnnotationDefault() {
        return null;
    }

    @Override
    public TreePrinter visitMethodAnnotation(String descriptor, boolean visible) {
        return null;
    }

    @Override
    public TreePrinter visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return null;
    }

    @Override
    public void visitMethodAttribute(Attribute attribute) {
        // do nothing
    }

    @Override
    public void visitCode() {
        // do nothing
    }

    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        // do nothing
    }

    @Override
    public void visitInsn(int opcode) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new InsnNode(%s));%n", OPCODES[opcode]);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new IntInsnNode(%s, %s));%n", OPCODES[opcode], opcode == Opcodes.NEWARRAY ? TYPES[operand] : Integer.toString(operand));
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new VarInsnNode(%s, %s));%n", OPCODES[opcode], var);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new TypeInsnNode(%s, %s));%n", OPCODES[opcode], toConstant(type));
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new FieldInsnNode(%s, %s, %s, %s));%n", OPCODES[opcode], toConstant(owner), toConstant(name), toConstant(descriptor));
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new MethodInsnNode(%s, %s, %s, %s, %s));%n", OPCODES[opcode], toConstant(owner), toConstant(name), toConstant(descriptor), isInterface ? "true" : "false");
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        String bootstrapMethodArgumentsString = "new Object[]{";

        for (int i = 0; i < bootstrapMethodArguments.length; ++i) {
            bootstrapMethodArgumentsString += toConstant(bootstrapMethodArguments[i]);
            if (i != bootstrapMethodArguments.length - 1) {
                bootstrapMethodArgumentsString += ", ";
            }
        }
        bootstrapMethodArgumentsString += "}";

        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new InvokeDynamicInsnNode(%s, %s, %s, %s));%n", toConstant(name), toConstant(descriptor), toConstant(bootstrapMethodHandle), bootstrapMethodArgumentsString);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String declareLabel = declareLabelNode(label);
        String labelName = toLabelName(label);
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        if (declareLabel != null && !"".equals(declareLabel)) {
            fm.format("        %s%n", declareLabel);
        }

        fm.format("        il.add(new JumpInsnNode(%s, %s));%n", OPCODES[opcode], labelName);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitLabel(Label label) {
        String labelName = toLabelName(label);
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("%n");
        fm.format("        il.add(%s);%n", labelName);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitLdcInsn(Object value) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new LdcInsnNode(%s));%n", toConstant(value));
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new IincInsnNode(%s, %s));%n", var, increment);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);

        for (Label label : labels) {
            String declareLabel = declareLabelNode(label);
            fm.format("        %s%n", declareLabel);
        }
        String defautLabel = declareLabelNode(dflt);

        StringBuilder labelsString = new StringBuilder("new LabelNode[] {");
        for (int i = 0; i < labels.length; ++i) {
            labelsString.append(i == 0 ? " " : ", ");
            labelsString.append(toLabelName(labels[i]));
        }
        labelsString.append(END_ARRAY);

        fm.format("        %s%n", defautLabel);
        fm.format("        il.add(new TableSwitchInsnNode(%s, %s, %s, %s));%n", min, max, toLabelName(dflt), labelsString);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);

        StringBuilder keyString = new StringBuilder("new int[] {");
        for (int i = 0; i < keys.length; ++i) {
            keyString.append(i == 0 ? " " : ", ");
            keyString.append(keys[i]);
        }
        keyString.append(" }");

        for (Label label : labels) {
            String declareLabel = declareLabelNode(label);
            fm.format("        %s%n", declareLabel);
        }
        String defautLabel = declareLabelNode(dflt);

        StringBuilder labelsString = new StringBuilder("new LabelNode[] {");
        for (int i = 0; i < labels.length; ++i) {
            labelsString.append(i == 0 ? " " : ", ");
            labelsString.append(toLabelName(labels[i]));
        }
        labelsString.append(END_ARRAY);

        fm.format("        %s%n", defautLabel);
        fm.format("        il.add(new LookupSwitchInsnNode(%s, %s, %s));%n", toLabelName(dflt), keyString, labelsString);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int numDimensions) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        il.add(new MultiANewArrayInsnNode(%s, %s));%n", toConstant(descriptor), numDimensions);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, String type) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        String startLabelNodeString = declareLabelNode(start);
        String endLabelNodeString = declareLabelNode(end);
        String handlerLabelNodeString = declareLabelNode(handler);
        if (startLabelNodeString != null && !"".equals(startLabelNodeString)) {
            fm.format("        %s%n", startLabelNodeString);
        }
        if (endLabelNodeString != null && !"".equals(endLabelNodeString)) {
            fm.format("        %s%n", endLabelNodeString);
        }
        if (handlerLabelNodeString != null && !"".equals(handlerLabelNodeString)) {
            fm.format("        %s%n", handlerLabelNodeString);
        }

        fm.format("        methodNode.tryCatchBlocks.add(new TryCatchBlockNode(%s, %s, %s, %s));%n", toLabelName(start), toLabelName(end), toLabelName(handler), toConstant(type));
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitLocalVariable(String name, String descriptor, String signature, Label start, Label end, int index) {
        // do nothing
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        // do nothing
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        stringBuilder.setLength(0);
        Formatter fm = new Formatter(stringBuilder);
        fm.format("        %n");
        fm.format("        methodNode.maxStack = %s;%n", maxStack);
        fm.format("        methodNode.maxLocals = %s;%n", maxLocals);
        text.add(stringBuilder.toString());
    }

    @Override
    public void visitMethodEnd() {
        // do nothing
    }

    // -----------------------------------------------------------------------------------------------
    // Utility methods
    // -----------------------------------------------------------------------------------------------
    protected TreePrinter createVariable(String visitorVariableName) {
        return new TreePrinter(api, visitorVariableName);
    }

    private String toAccessFlags(final int accessFlags) {
        StringBuilder sb = new StringBuilder();
        boolean isEmpty = true;
        if ((accessFlags & Opcodes.ACC_PUBLIC) != 0) {
            sb.append("ACC_PUBLIC");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_PRIVATE) != 0) {
            sb.append("ACC_PRIVATE");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_PROTECTED) != 0) {
            sb.append("ACC_PROTECTED");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_FINAL) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            if ((accessFlags & ACCESS_MODULE) == 0) {
                sb.append("ACC_FINAL");
            }
            else {
                sb.append("ACC_TRANSITIVE");
            }
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_STATIC) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_STATIC");
            isEmpty = false;
        }
        if ((accessFlags & (Opcodes.ACC_SYNCHRONIZED | Opcodes.ACC_SUPER | Opcodes.ACC_TRANSITIVE))
                != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            if ((accessFlags & ACCESS_CLASS) == 0) {
                if ((accessFlags & ACCESS_MODULE) == 0) {
                    sb.append("ACC_SYNCHRONIZED");
                }
                else {
                    sb.append("ACC_TRANSITIVE");
                }
            }
            else {
                sb.append("ACC_SUPER");
            }
            isEmpty = false;
        }
        if ((accessFlags & (Opcodes.ACC_VOLATILE | Opcodes.ACC_BRIDGE | Opcodes.ACC_STATIC_PHASE))
                != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            if ((accessFlags & ACCESS_FIELD) == 0) {
                if ((accessFlags & ACCESS_MODULE) == 0) {
                    sb.append("ACC_BRIDGE");
                }
                else {
                    sb.append("ACC_STATIC_PHASE");
                }
            }
            else {
                sb.append("ACC_VOLATILE");
            }
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_VARARGS) != 0
                && (accessFlags & (ACCESS_CLASS | ACCESS_FIELD)) == 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_VARARGS");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_TRANSIENT) != 0 && (accessFlags & ACCESS_FIELD) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_TRANSIENT");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_NATIVE) != 0
                && (accessFlags & (ACCESS_CLASS | ACCESS_FIELD)) == 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_NATIVE");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_ENUM) != 0
                && (accessFlags & (ACCESS_CLASS | ACCESS_FIELD | ACCESS_INNER)) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_ENUM");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_ANNOTATION) != 0
                && (accessFlags & (ACCESS_CLASS | ACCESS_INNER)) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_ANNOTATION");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_ABSTRACT) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_ABSTRACT");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_INTERFACE) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_INTERFACE");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_STRICT) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_STRICT");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_SYNTHETIC) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_SYNTHETIC");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_DEPRECATED) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_DEPRECATED");
            isEmpty = false;
        }
        if ((accessFlags & Opcodes.ACC_RECORD) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            sb.append("ACC_RECORD");
            isEmpty = false;
        }
        if ((accessFlags & (Opcodes.ACC_MANDATED | Opcodes.ACC_MODULE)) != 0) {
            if (!isEmpty) {
                sb.append(" | ");
            }
            if ((accessFlags & ACCESS_CLASS) == 0) {
                sb.append("ACC_MANDATED");
            }
            else {
                sb.append("ACC_MODULE");
            }
            isEmpty = false;
        }
        if (isEmpty) {
            sb.append('0');
        }
        return sb.toString();
    }

    protected String toConstant(final Object value) {
        StringBuilder sb = new StringBuilder();
        if (value == null) {
            sb.append("null");
        }
        else if (value instanceof String) {
            appendString(sb, (String) value);
        }
        else if (value instanceof Type) {
            sb.append("Type.getType(\"");
            sb.append(((Type) value).getDescriptor());
            sb.append("\")");
        }
        else if (value instanceof Handle) {
            sb.append("new Handle(");
            Handle handle = (Handle) value;
            sb.append("Opcodes.").append(HANDLE_TAG[handle.getTag()]).append(", \"");
            sb.append(handle.getOwner()).append(COMMA);
            sb.append(handle.getName()).append(COMMA);
            sb.append(handle.getDesc()).append("\", ");
            sb.append(handle.isInterface()).append(")");
        }
        else if (value instanceof ConstantDynamic) {
            sb.append("new ConstantDynamic(\"");
            ConstantDynamic constantDynamic = (ConstantDynamic) value;
            sb.append(constantDynamic.getName()).append(COMMA);
            sb.append(constantDynamic.getDescriptor()).append("\", ");
            sb.append(toConstant(constantDynamic.getBootstrapMethod()));

            sb.append(NEW_OBJECT_ARRAY);
            int bootstrapMethodArgumentCount = constantDynamic.getBootstrapMethodArgumentCount();
            for (int i = 0; i < bootstrapMethodArgumentCount; ++i) {
                sb.append(toConstant(constantDynamic.getBootstrapMethodArgument(i)));
                if (i != bootstrapMethodArgumentCount - 1) {
                    sb.append(", ");
                }
            }
            sb.append("})");
        }
        else if (value instanceof Byte) {
            sb.append("new Byte((byte)").append(value).append(')');
        }
        else if (value instanceof Boolean) {
            sb.append(((Boolean) value).booleanValue() ? "Boolean.TRUE" : "Boolean.FALSE");
        }
        else if (value instanceof Short) {
            sb.append("new Short((short)").append(value).append(')');
        }
        else if (value instanceof Character) {
            sb
                    .append("new Character((char)")
                    .append((int) ((Character) value).charValue())
                    .append(')');
        }
        else if (value instanceof Integer) {
            sb.append("new Integer(").append(value).append(')');
        }
        else if (value instanceof Float) {
            sb.append("new Float(\"").append(value).append("\")");
        }
        else if (value instanceof Long) {
            sb.append("new Long(").append(value).append("L)");
        }
        else if (value instanceof Double) {
            sb.append("new Double(\"").append(value).append("\")");
        }
        else if (value instanceof byte[]) {
            byte[] byteArray = (byte[]) value;
            sb.append("new byte[] {");
            for (int i = 0; i < byteArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append(byteArray[i]);
            }
            sb.append('}');
        }
        else if (value instanceof boolean[]) {
            boolean[] booleanArray = (boolean[]) value;
            sb.append("new boolean[] {");
            for (int i = 0; i < booleanArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append(booleanArray[i]);
            }
            sb.append('}');
        }
        else if (value instanceof short[]) {
            short[] shortArray = (short[]) value;
            sb.append("new short[] {");
            for (int i = 0; i < shortArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append("(short)").append(shortArray[i]);
            }
            sb.append('}');
        }
        else if (value instanceof char[]) {
            char[] charArray = (char[]) value;
            sb.append("new char[] {");
            for (int i = 0; i < charArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append("(char)").append((int) charArray[i]);
            }
            sb.append('}');
        }
        else if (value instanceof int[]) {
            int[] intArray = (int[]) value;
            sb.append("new int[] {");
            for (int i = 0; i < intArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append(intArray[i]);
            }
            sb.append('}');
        }
        else if (value instanceof long[]) {
            long[] longArray = (long[]) value;
            sb.append("new long[] {");
            for (int i = 0; i < longArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append(longArray[i]).append('L');
            }
            sb.append('}');
        }
        else if (value instanceof float[]) {
            float[] floatArray = (float[]) value;
            sb.append("new float[] {");
            for (int i = 0; i < floatArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append(floatArray[i]).append('f');
            }
            sb.append('}');
        }
        else if (value instanceof double[]) {
            double[] doubleArray = (double[]) value;
            sb.append("new double[] {");
            for (int i = 0; i < doubleArray.length; i++) {
                sb.append(i == 0 ? "" : ",").append(doubleArray[i]).append('d');
            }
            sb.append('}');
        }
        return sb.toString();
    }

    protected String declareLabelNode(final Label label) {
        if (labelNames == null) {
            labelNames = new HashMap<>();
        }

        String labelName = labelNames.get(label);
        if (labelName == null) {
            labelName = "labelNode" + labelNames.size();
            labelNames.put(label, labelName);
            StringBuilder sb = new StringBuilder();
            sb.append("LabelNode ").append(labelName).append(" = new LabelNode();");
            return sb.toString();
        }
        return "";
    }

    protected String toLabelName(final Label label) {
        return labelNames.get(label);
    }
}
