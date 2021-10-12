package lsieun.asm.core.empty;

import org.objectweb.asm.AnnotationVisitor;

public class EmptyAnnotationVisitor extends AnnotationVisitor {
    public EmptyAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor);
    }
}
