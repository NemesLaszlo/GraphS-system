package backend;

public class CustomVertex {
    private String id;
    private boolean hotPoint;

    public CustomVertex(String id) {
        this.id = id;
        this.hotPoint = false;
    }

    public String getId() {
        return this.id;
    }

    public boolean getHotPoint() {
        return this.hotPoint;
    }

    public void setHotPoint(boolean hotPoint) {
        this.hotPoint = hotPoint;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
