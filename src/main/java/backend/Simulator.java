package backend;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

public class Simulator {

    private Graph<CustomVertex, CustomEdge> graph;
    private Graph<CustomVertex, CustomEdge> graphRev;
    private Graph<CustomVertex, HotpointEdge> graphHotpoint;
    private GraphGenerator generator;

    private long actualTimeStamp;
    private final int graphHotpointThreshold = 10;
    private final int pathLength = 3;

    /**
     * Simulator Constructor
     */
    public Simulator(int numberOfNodes) {
        this.generator = new GraphGenerator();
        this.actualTimeStamp = 0;
        createDynamicGraph(numberOfNodes);

    }

    /**
     * Getter to the graph.
     * @return With tha actual graph.
     */
    public Graph<CustomVertex, CustomEdge> getGraph() {
        return graph;
    }

    public Graph<CustomVertex, CustomEdge> getGraphRev() {
        return graphRev;
    }

    public Graph<CustomVertex, HotpointEdge> getGraphHotpoint() {
        return graphHotpoint;
    }

    /**
     * Create a Dynamic Graph structure, with the Node number of the parameter.
     * @param numberOfNodes - number of the nodes to create a graph structure.
     */
    public void createDynamicGraph(int numberOfNodes) {
        this.graph = this.generator.createDynamicGraph(numberOfNodes);
        this.graphRev = new DefaultDirectedGraph<CustomVertex, CustomEdge>(CustomEdge.class);
        this.graphHotpoint = new DefaultDirectedGraph<CustomVertex, HotpointEdge>(HotpointEdge.class);

        Set<CustomVertex> verts = graph.vertexSet();
        Set<CustomEdge> edges = graph.edgeSet();

        for (CustomVertex v : verts) {
            graphRev.addVertex(v);
        }

        for (CustomEdge e : edges) {
            graphRev.addEdge(e.getTarget(),e.getSource(), new CustomEdge(e.getIsStatic(),e.getTimeStamp()));
        }

        recalculateHotpoints(verts);
    }

    /**
     * Dynamic change call.
     */

    private void findAllSimplePath(CustomVertex start, CustomVertex end, Set<CustomVertex> visited, Stack<CustomVertex> stack ,List<List<CustomVertex>> pathList) {

        visited.add(start);
        stack.push(start);

        if (start.equals(end)) {

            List<CustomVertex> path = new LinkedList();
            for (CustomVertex v : stack) {
                path.add(v);
            }
            pathList.add(path);
        } else {
            if (stack.size() < pathLength) {
                for (CustomEdge e : graph.outgoingEdgesOf(start)) {
                    if (!visited.contains(e.getTarget())) {
                        findAllSimplePath(e.getTarget(),end,visited,stack,pathList);
                    }
                }
            }
        }
        stack.pop();
        visited.remove(start);

    }

    private CustomVertex pickRandomVertex() {
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

    private void RemoveEdgesByTimeStamp(Graph<CustomVertex, CustomEdge> dynamicGraph, Set<CustomVertex> changedVerts) {

        ArrayList<CustomEdge> toRemove = new ArrayList<CustomEdge>();
        for(CustomEdge edge: dynamicGraph.edgeSet()) {
            if(!edge.getIsStatic() && edge.getTimeStamp() <= actualTimeStamp) {
                toRemove.add(edge);
                changedVerts.add(edge.getSource());
                changedVerts.add(edge.getTarget());
                System.out.println("Delete Edge Change");
            }
        }
        dynamicGraph.removeAllEdges(toRemove);

    }

    private void recalculateHotpoints(Set<CustomVertex> verts) {

        List<CustomVertex> newHotPoints = new LinkedList<CustomVertex>();

        for (CustomVertex v : verts) {
            if (graph.edgesOf(v).size() >= graphHotpointThreshold && !v.getHotPoint()) {
                graphHotpoint.addVertex(v);
                v.setHotPoint(true);
                newHotPoints.add(v);
                System.out.println("New HP: " + v + graph.edgesOf(v).size());
            } else if(graph.edgesOf(v).size() < graphHotpointThreshold && v.getHotPoint()) {
                graphHotpoint.removeVertex(v);
                v.setHotPoint(false);
                System.out.println("Byebye HP: " + v);
            }
        }

        for (CustomVertex v : newHotPoints) {
            for (CustomVertex hp : graphHotpoint.vertexSet()) {
                if (!v.equals(hp)) {
                    Set<CustomVertex> visited = new HashSet<CustomVertex>();
                    Stack<CustomVertex> stack = new Stack<CustomVertex>();
                    List<List<CustomVertex>> pathList = new LinkedList<List<CustomVertex>>();
                    findAllSimplePath(v,hp,visited,stack,pathList);

                    for (List<CustomVertex> path : pathList) {
                        graphHotpoint.addEdge(v,hp,new HotpointEdge(path));
                    }
                }
            }
        }


    }

    public void dynamicChangeNext() {

        Set<CustomVertex> changedVerts = new HashSet<CustomVertex>();

        CustomVertex firstVertex = pickRandomVertex();
        CustomVertex secVertex = pickRandomVertex();
        System.out.println("first id: " + firstVertex.getId() + " sec id: " + secVertex.getId() );
        if(new Random().nextDouble() <= 0.75) {
            if(firstVertex != null && secVertex != null) {
                if(!firstVertex.getId().equals(secVertex.getId())) {
                    if(Graphs.successorListOf(graph, firstVertex).contains(secVertex) && Graphs.successorListOf(graph, secVertex).contains(firstVertex)) {
                        System.out.println("There is a edge between this two vertices.");
                    } else if(!Graphs.successorListOf(graph, firstVertex).contains(secVertex)) {
                        Random random = new Random();
                        int time = random.nextInt(10) + 1;
                        graph.addEdge(firstVertex, secVertex, new CustomEdge(false, this.actualTimeStamp + time));
                        graphRev.addEdge(secVertex, firstVertex, new CustomEdge(false, this.actualTimeStamp + time));

                        changedVerts.add(firstVertex);
                        changedVerts.add(secVertex);

                        System.out.println("Add Edge Change");
                    }
                }
            }
        }

        RemoveEdgesByTimeStamp(graph, changedVerts);
        RemoveEdgesByTimeStamp(graphRev);

        recalculateHotpoints(changedVerts);

        /*for(CustomVertex vertex: dynamicGraph.vertexSet()) {
            System.out.println("Vertex id: " + vertex.getId() + " Degree num: " +  dynamicGraph.outDegreeOf(vertex));
            System.out.println("Vertex id: " + vertex.getId() + " Edges to: " +  Graphs.successorListOf(dynamicGraph, vertex));
        }*/

        this.actualTimeStamp++;
        System.out.println("Actual time: " + this.actualTimeStamp);
    }


    public void GraphSSystem() {
        this.dynamicChangeNext();
        // TODO: GraphS system
    }

}
