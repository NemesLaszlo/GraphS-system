package backend;

import org.jgrapht.Graph;

public class Simulator {

    private Graph<CustomVertex, CustomEdge> graph;
    private GraphGenerator generator;

    /**
     * Simulator Constructor
     */
    public Simulator() {
        this.generator = new GraphGenerator(0);
        this.graph = this.generator.createDynamicGraph(7);
    }

    /**
     * Getter to the graph.
     * @return With tha actual graph.
     */
    public Graph<CustomVertex, CustomEdge> getGraph() {
        return this.graph;
    }

    /**
     * Create a Dynamic Graph structure, with the Node number of the parameter.
     * @param numberOfNodes - number of the nodes to create a graph structure.
     */
    public void createDynamicGraph(int numberOfNodes) {
        this.graph = this.generator.createDynamicGraph(numberOfNodes);
    }

    /**
     * Dynamic change call.
     */
    public void dynamicChangeNext() {
        generator.dynamicChange(this.graph);
    }


    public void GraphSSystem() {
        this.dynamicChangeNext();
        // TODO: GraphS system
    }

}
