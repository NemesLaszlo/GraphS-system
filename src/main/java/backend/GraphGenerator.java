package backend;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.*;

import java.lang.reflect.Array;
import java.util.*;

public class GraphGenerator {
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
    public Graph<CustomVertex, CustomEdge> createDynamicGraph(int numberOfNodes, int graphHotpointThreshold){
        Graph<CustomVertex, CustomEdge> dynamicGraph = crateEmptyGraph();
        List<CustomVertex> dynamicNodes = createGraphNodes(numberOfNodes);
        Map<CustomVertex,ArrayList<CustomVertex>> temp = new HashMap<>();

        for(CustomVertex node : dynamicNodes){
            dynamicGraph.addVertex(node);
        }

        for(CustomVertex node : dynamicNodes) {
            ArrayList<CustomVertex> t = new ArrayList<>();
            for (CustomVertex v : dynamicNodes) {
                if (v != node) {
                    t.add(v);
                }
            }
            temp.put(node,t);
        }
        Random rand = new Random(1);

        int basicEdges = 2*dynamicNodes.size();

        for (int i=0;i<basicEdges;i++) {
            CustomVertex start = dynamicNodes.get(rand.nextInt(dynamicNodes.size()));
            dynamicGraph.addEdge(start,temp.get(start).remove(rand.nextInt(temp.get(start).size())),new CustomEdge(true, 5));
        }

        int hotPointCount = rand.nextInt(dynamicNodes.size()/5)+1;

        for (int i=0;i<hotPointCount;i++) {
            CustomVertex start = dynamicNodes.get(rand.nextInt(dynamicNodes.size()));
            int max = Math.min(graphHotpointThreshold, temp.get(start).size());
            System.out.println(start);
            System.out.println(temp.get(start).size());
            for (int j = 0; j < max;j++) {
                dynamicGraph.addEdge(start,temp.get(start).remove(rand.nextInt(temp.get(start).size())),new CustomEdge(true, 5));
            }
        }



//        double rarity = 0.05;


//        for(int i = 0; i < dynamicNodes.size(); ++i){
//            CustomVertex buffer = dynamicNodes.get(i);
//            for (CustomVertex node : dynamicNodes) {
//                if (!buffer.getId().equals(node.getId())) {
//                    if( rand.nextDouble() <= rarity ) {
//                        dynamicGraph.addEdge(buffer, node, new CustomEdge(true, 5));
//                    }
//                }
//            }
//        }
//        for(CustomVertex vertex : dynamicGraph.vertexSet()) {
//            List<CustomVertex> connectionsTo = Graphs.successorListOf(dynamicGraph, vertex);
//            for(CustomVertex connection : connectionsTo) {
//                if(!Graphs.successorListOf(dynamicGraph, connection).contains(vertex)) {
//                    if(rand.nextDouble() <= rarity) {
//                        dynamicGraph.addEdge(connection, vertex, new CustomEdge(true, 5));
//                    }
//                }
//            }
//        }

        return dynamicGraph;
    }

    /**
     * Dynamic Graph - dynamic function add random edge to the graph or delete edge by timeStamp or do nothing in the step.
     * @param dynamicGraph - actual dynamic graph, where we make changes with the edges.
     */
    public void dynamicChange(Graph<CustomVertex, CustomEdge> dynamicGraph) {

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
