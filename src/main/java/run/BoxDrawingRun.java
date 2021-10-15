package run;

import lsieun.utils.BoxDrawingUtils;

public class BoxDrawingRun {
    private static final String[][] Interpreter_Value_Matrix = {
            {"0", "Interpreter", "Value"},
            {"1", "BasicInterpreter", "BasicValue"},
            {"2", "BasicVerifier", "BasicValue"},
            {"3", "SimpleVerifier", "BasicValue"},
            {"4", "SourceInterpreter", "SourceValue"},
    };

    private static final String[][] Analyzer_Frame_Interpreter_Value_Matrix = {
            {"Fixed", "Analyzer"},
            {"", "Frame"},
            {"Variable", "Interpreter"},
            {"", "Value"},
    };

    public static void main(String[] args) {
        BoxDrawingUtils.printTable(Analyzer_Frame_Interpreter_Value_Matrix);
    }
}
