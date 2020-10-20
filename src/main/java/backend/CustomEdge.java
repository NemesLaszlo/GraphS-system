package backend;

import org.jgrapht.graph.DefaultEdge;

public class CustomEdge extends DefaultEdge {

    private long timeStamp;
    private boolean isStatic;

    public CustomEdge(boolean isStatic, long timeStamp) {
        this.isStatic = isStatic;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public boolean getIsStatic() {
        return this.isStatic;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public String toString() {
        return "(" + getIsStatic() + " : " + getTimeStamp() + "   " +  super.toString() +  ")";
    }

}
