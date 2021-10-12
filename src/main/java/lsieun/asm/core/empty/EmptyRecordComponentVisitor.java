package lsieun.asm.core.empty;

import org.objectweb.asm.RecordComponentVisitor;

public class EmptyRecordComponentVisitor extends RecordComponentVisitor {
    public EmptyRecordComponentVisitor(int api, RecordComponentVisitor recordComponentVisitor) {
        super(api, recordComponentVisitor);
    }
}
