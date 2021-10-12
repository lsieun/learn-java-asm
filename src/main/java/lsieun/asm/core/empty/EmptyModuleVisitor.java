package lsieun.asm.core.empty;

import org.objectweb.asm.ModuleVisitor;

public class EmptyModuleVisitor extends ModuleVisitor {
    public EmptyModuleVisitor(int api, ModuleVisitor moduleVisitor) {
        super(api, moduleVisitor);
    }
}
