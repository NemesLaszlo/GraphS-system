package backend;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;

import java.lang.reflect.Array;
import java.util.*;

public class GraphGenerator {

    private long actualTimeStamp;

    public GraphGenerator(long actualTime) {
        this.actualTimeStamp = actualTime;
    }

    /**
     * Initialize an empty DirectedGraph.
     * @return Graph<CustomVertex, CustomEdge>
     */
    private Graph<CustomVertex, CustomEdge> crateEmptyGraph(){
        Graph<CustomVertex, CustomEdge> graph;
        graph = new DefaultDirectedGraph<CustomVertex, CustomEdge>(CustomEdge.class);
        return graph;
    }

    /**
     * Create a list from the the created Node objects by the parameter number,
     * which can we use to create a Graph structure.
     * @param numberOfNodes - number of the nodes to create a graph structure.
     * @return List with the Nodes to the graph.
     */
    private List<CustomVertex> createGraphNodes(int numberOfNodes){
        List<CustomVertex> nodes = new ArrayList<CustomVertex>();
        for(int i = 0; i < numberOfNodes; ++i){
            nodes.add(new CustomVertex(String.valueOf(i)));
        }
        return nodes;
    }

    /**
     * Create a Dynamic Graph structure, with the Node number of the parameter.
     * @param numberOfNodes - number of the nodes to create a graph structure.
     * @return Graph - Dynamic "CliqueGraph" structure, only the frame without dynamic change.
     */
    public Graph<CustomVertex, CustomEdge> createDynamicGraph(int numberOfNodes){
        Graph<CustomVertex, CustomEdge> dynamicGraph = crateEmptyGraph();
        List<CustomVertex> dynamicNodes = createGraphNodes(numberOfNodes);

        for(CustomVertex node : dynamicNodes){
            dynamicGraph.addVertex(node);
        }
        for(int i = 0; i < dynamicNodes.size(); ++i){
            CustomVertex buffer = dynamicNodes.get(i);
            for (CustomVertex node : dynamicNodes) {
                if (!buffer.getId().equals(node.getId())) {
                    if( new Random().nextDouble() <= 0.3 ) {
                        dynamicGraph.addEdge(buffer, node, new CustomEdge(true, 5));
                    }
                }
            }
        }
        for(CustomVertex vertex : dynamicGraph.vertexSet()) {
            List<CustomVertex> connectionsTo = Graphs.successorListOf(dynamicGraph, vertex);
            for(CustomVertex connection : connectionsTo) {
                if(!Graphs.successorListOf(dynamicGraph, connection).contains(vertex)) {
                    if(new Random().nextDouble() <= 0.3) {
                        dynamicGraph.addEdge(connection, vertex, new CustomEdge(true, 5));
                    }
                }
            }
        }

        return dynamicGraph;
    }

    /**
     * Dynamic Graph - dynamic function add random edge to the graph or delete edge by timeStamp or do nothing in the step.
     * @param dynamicGraph - actual dynamic graph, where we make changes with the edges.
     */
    public void dynamicChange(Graph<CustomVertex, CustomEdge> dynamicGraph) {
        CustomVertex firstVertex = pickRandomVertex(dynamicGraph);
        CustomVertex secVertex = pickRandomVertex(dynamicGraph);
        System.out.println("first id: " + firstVertex.getId() + " sec id: " + secVertex.getId() );
        if(new Random().nextDouble() <= 0.75) {
            if(firstVertex != null && secVertex != null) {
                if(!firstVertex.getId().equals(secVertex.getId())) {
                    if(Graphs.successorListOf(dynamicGraph, firstVertex).contains(secVertex) && Graphs.successorListOf(dynamicGraph, secVertex).contains(firstVertex)) {
                        System.out.println("There is a edge between this two vertices.");
                    } else if(!Graphs.successorListOf(dynamicGraph, firstVertex).contains(secVertex)) {
                        Random random = new Random();
                        dynamicGraph.addEdge(firstVertex, secVertex, new CustomEdge(false, this.actualTimeStamp + random.nextInt(10) + 1 ));
                        System.out.println("Add Edge Change");
                    }
                }
            }
        }
        RemoveEdgesByTimeStamp(dynamicGraph);

        /*for(CustomVertex vertex: dynamicGraph.vertexSet()) {
            System.out.println("Vertex id: " + vertex.getId() + " Degree num: " +  dynamicGraph.outDegreeOf(vertex));
            System.out.println("Vertex id: " + vertex.getId() + " Edges to: " +  Graphs.successorListOf(dynamicGraph, vertex));
        }*/

        this.actualTimeStamp++;
        System.out.println("Actual time: " + this.actualTimeStamp);
    }

    /**
     * Pick a random vertex from the parameter graph.
     * @param graph - graph, where we pick a random vertex
     */
    private CustomVertex pickRandomVertex(Graph<CustomVertex, CustomEdge> graph) {
        CustomVertex result = null;
        int size = graph.vertexSet().size();
        int item = new Random().nextInt(size);
        int i = 0;
        for(CustomVertex vertex : graph.vertexSet())
        {
            if (i == item)
                result = vertex;
            i++;
        }
        return result;
    }

    /**
     * Remove every edge, where the timeStamp expired.
     * @param dynamicGraph - actual dynamic graph
     */
    private void RemoveEdgesByTimeStamp(Graph<CustomVertex, CustomEdge> dynamicGraph) {
        ArrayList<CustomEdge> toRemove = new ArrayList<CustomEdge>();
        for(CustomEdge edge: dynamicGraph.edgeSet()) {
            if(!edge.getIsStatic() && edge.getTimeStamp() <= actualTimeStamp) {
                toRemove.add(edge);
                System.out.println("Delete Edge Change");
            }
        }
        dynamicGraph.removeAllEdges(toRemove);

    }

    /**
     * Print informations about the graph -> graph nodes and graph edges, plus about the structure.
     * @param g - the graph where we would like to see the informations about that.
     */
    public void consoleGraphInfo(Graph<CustomVertex, CustomEdge> g){
        System.out.println("Graph system:");
        for(CustomVertex node : g.vertexSet()){
            System.out.println(node.toString());
            System.out.println(g.edgesOf(node));
        }
        System.out.println();
        System.out.println("Graph:");
        System.out.println(g.toString());
        System.out.println("Graph only the edges:");
        System.out.println(g.edgeSet());
    }

}
