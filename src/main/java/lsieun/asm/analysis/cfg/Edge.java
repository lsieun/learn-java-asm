package lsieun.asm.analysis.cfg;

public final class Edge {
    public final int from;
    public final int to;

    public Edge(int from, int to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Edge)) {
            return false;
        }
        Edge edge = (Edge) obj;
        return (from == edge.from) && (to == edge.to);
    }

    @Override
    public int hashCode() {
        return 31 * from + to;
    }
}