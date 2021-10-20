package run;

import lsieun.utils.BoxDrawingUtils;

public class BoxDrawingRun {
    private static final String[][] Interpreter_Value_Matrix = {
            {"0", "Interpreter", "Value", "Range"},
            {"1", "BasicInterpreter", "BasicValue", "7"},
            {"2", "BasicVerifier", "BasicValue", "7"},
            {"3", "SimpleVerifier", "BasicValue", "N"},
            {"4", "SourceInterpreter", "SourceValue", "N"},
    };

    private static final String[][] Analyzer_Frame_Interpreter_Value_Matrix = {
            {"Fixed", "Analyzer"},
            {"", "Frame"},
            {"Variable", "Interpreter"},
            {"", "Value"},
    };

    private static final String[][] Top_Null_Void = {
            {"", "ASM Type", "Frame Value"},
            {"top", "null", "BasicValue.UNINITIALIZED_VALUE"},
            {"aconst_null", "BasicInterpreter.NULL_TYPE", "BasicValue.REFERENCE_VALUE"},
            {"void", "Type.VOID_TYPE", "null"},
    };

    public static void main(String[] args) {
        BoxDrawingUtils.printTable(Top_Null_Void);
    }
}
