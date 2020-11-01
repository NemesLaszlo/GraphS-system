package backend;

import org.jgrapht.graph.DefaultEdge;

import java.util.List;

public class HotpointEdge extends DefaultEdge {

    private List<CustomVertex> path;

    public HotpointEdge(List<CustomVertex> path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "HE: (" + this.path.size() + "   " +  super.toString() +  ")";
    }

}
