package lsieun.asm.template;

public class Info {
    public final int srcOpcode;
    public final String srcOwner;
    public final String srcName;
    public final String srcDesc;

    public final int targetOpcode;
    public final String targetOwner;
    public final String targetName;
    public final String targetDesc;

    public Info(
            int srcOpcode, String srcOwner, String srcName, String srcDesc,
            int targetOpcode, String targetOwner, String targetName, String targetDesc) {
        this.srcOpcode = srcOpcode;
        this.srcOwner = srcOwner;
        this.srcName = srcName;
        this.srcDesc = srcDesc;
        this.targetOpcode = targetOpcode;
        this.targetOwner = targetOwner;
        this.targetName = targetName;
        this.targetDesc = targetDesc;
    }
}
