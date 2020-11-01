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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomVertex that = (CustomVertex) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
