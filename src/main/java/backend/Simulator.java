package backend;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator {

    private Graph<CustomVertex, CustomEdge> graph;
    private Graph<CustomVertex, CustomEdge> graphRev;
    private Graph<CustomVertex, HotpointEdge> graphHotpoint;
    private GraphGenerator generator;

    private long actualTimeStamp;
    private final int graphHotpointThreshold = 15;
    private final int pathLength = 4;

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

    public void setActualTimeStamp(long actualTimeStamp) {
        this.actualTimeStamp = actualTimeStamp;
    }

    /**
     * Create a Dynamic Graph structure, with the Node number of the parameter.
     * @param numberOfNodes - number of the nodes to create a graph structure.
     */
    public void createDynamicGraph(int numberOfNodes) {
        this.graph = this.generator.createDynamicGraph(numberOfNodes, graphHotpointThreshold);
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

    private void findCloseHP(CustomVertex start, Graph<CustomVertex,CustomEdge> graph, Set<CustomVertex> visited, Stack<CustomVertex> stack, Set<CustomVertex> hps) {

        visited.add(start);
        stack.push(start);
        if (stack.size() < pathLength) {
            for (CustomEdge e : graph.outgoingEdgesOf(start)) {
                if (e.getTarget().getHotPoint()) {
                    hps.add(e.getTarget());
                    continue;
                }
                if (!visited.contains(e.getTarget())) {
                    findCloseHP(e.getTarget(),graph,visited,stack,hps);
                }
            }
        }
        stack.pop();
        visited.remove(start);
    }

    private void findAllSimplePathHP(CustomVertex start, CustomVertex end, Graph<CustomVertex,CustomEdge> graph, Set<CustomVertex> visited, Stack<CustomVertex> stack ,List<List<CustomVertex>> pathList, Set<CustomVertex> hps) {

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
                    if (e.getTarget().getHotPoint()) {
                        hps.add(e.getTarget());
                        continue;
                    }
                    if (!visited.contains(e.getTarget())) {
                        findAllSimplePathHP(e.getTarget(),end,graph, visited,stack,pathList, hps);
                    }
                }
            }
        }
        stack.pop();
        visited.remove(start);
    }

    private void findAllSimplePath(CustomVertex start, CustomVertex end, Graph<CustomVertex,CustomEdge> graph, Set<CustomVertex> visited, Stack<CustomVertex> stack ,List<List<CustomVertex>> pathList) {

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
                    if (e.getTarget().getHotPoint() && e.getTarget() != end) {
                        continue;
                    }
                    if (!visited.contains(e.getTarget())) {
                        findAllSimplePath(e.getTarget(),end, graph, visited,stack,pathList);
                    }
                }
            }
        }
        stack.pop();
        visited.remove(start);
    }

    private void findAllSimplePathBetweenHP(CustomVertex start, CustomVertex end, Set<CustomVertex> visited, Stack<CustomVertex> stack ,List<List<CustomVertex>> pathList) {

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
                    if (e.getTarget().getHotPoint() && e.getTarget() != end) {
                        continue;
                    }
                    if (!visited.contains(e.getTarget())) {
                        findAllSimplePath(e.getTarget(),end, graph, visited,stack,pathList);
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
            }
        }
        dynamicGraph.removeAllEdges(toRemove);

    }

    private void recalculateHotpoints(Set<CustomVertex> verts) {

        List<CustomVertex> changedHotPoints = new LinkedList<CustomVertex>();

        for (CustomVertex v : verts) {
            if (graph.edgesOf(v).size() >= graphHotpointThreshold && !v.getHotPoint()) {
                graphHotpoint.addVertex(v);
                v.setHotPoint(true);
            } else if(graph.edgesOf(v).size() < graphHotpointThreshold && v.getHotPoint()) {
                graphHotpoint.removeVertex(v);
                v.setHotPoint(false);
            }
            if (v.getHotPoint()) {
                changedHotPoints.add(v);
            }
        }

        for (CustomVertex v : changedHotPoints) {
            for (CustomVertex hp : graphHotpoint.vertexSet()) {
                if (!v.equals(hp)) {
                    Set<CustomVertex> visited = new HashSet<CustomVertex>();
                    Stack<CustomVertex> stack = new Stack<CustomVertex>();
                    List<List<CustomVertex>> pathList = new LinkedList<List<CustomVertex>>();
                    findAllSimplePathBetweenHP(v,hp,visited,stack,pathList);

                    graphHotpoint.removeAllEdges(v,hp);

                    for (List<CustomVertex> path : pathList) {
                        graphHotpoint.addEdge(v,hp,new HotpointEdge(path));
                    }

                    visited.clear();
                    stack.clear();
                    pathList.clear();
                    findAllSimplePathBetweenHP(hp,v,visited,stack,pathList);

                    graphHotpoint.removeAllEdges(v,hp);

                    for (List<CustomVertex> path : pathList) {
                        graphHotpoint.addEdge(hp,v,new HotpointEdge(path));
                    }
                }
            }
        }


    }

    public void dynamicChangeNext() {

        Set<CustomVertex> changedVerts = new HashSet<CustomVertex>();

        Random rand = new Random();

        int newEdgeCount = rand.nextInt(graph.vertexSet().size() / 10 + 1);

        Set<CustomVertex> visited = new HashSet<CustomVertex>();
        Stack<CustomVertex> stack = new Stack<CustomVertex>();
        List<List<CustomVertex>> pathList = new LinkedList<List<CustomVertex>>();

        for (int i = 0; i < newEdgeCount; i++) {
            CustomVertex firstVertex = pickRandomVertex();
            CustomVertex secVertex = pickRandomVertex();
            System.out.println("first id: " + firstVertex.getId() + " sec id: " + secVertex.getId() );
            if(firstVertex != null && secVertex != null) {
                if(!firstVertex.getId().equals(secVertex.getId())) {
                    if(Graphs.successorListOf(graph, firstVertex).contains(secVertex) && Graphs.successorListOf(graph, secVertex).contains(firstVertex)) {
                        System.out.println("There is a edge between this two vertices.");
                    } else if(!Graphs.successorListOf(graph, firstVertex).contains(secVertex)) {
                        Random random = new Random();
                        int time = random.nextInt(10) + 5;
                        addDynamicEdge(firstVertex, secVertex);
                        graph.addEdge(firstVertex, secVertex, new CustomEdge(false, this.actualTimeStamp + time));
                        graphRev.addEdge(secVertex, firstVertex, new CustomEdge(false, this.actualTimeStamp + time));

//                        changedVerts.add(firstVertex);
//                        changedVerts.add(secVertex);

                        findCloseHP(firstVertex, graph, visited, stack, changedVerts);
                        visited.clear();
                        stack.clear();
                        findCloseHP(secVertex, graphRev, visited, stack, changedVerts);

                        System.out.println("Add Edge Change");
                    }
                }
            }
        }
        RemoveEdgesByTimeStamp(graph, changedVerts);
        RemoveEdgesByTimeStamp(graphRev);

        System.out.println(changedVerts);
        recalculateHotpoints(changedVerts);


        /*for(CustomVertex vertex: dynamicGraph.vertexSet()) {
            System.out.println("Vertex id: " + vertex.getId() + " Degree num: " +  dynamicGraph.outDegreeOf(vertex));
            System.out.println("Vertex id: " + vertex.getId() + " Edges to: " +  Graphs.successorListOf(dynamicGraph, vertex));
        }*/

        this.actualTimeStamp++;
        System.out.println("Actual time: " + this.actualTimeStamp);
    }

    public void addDynamicEdge(CustomVertex v1, CustomVertex v2) {

        if (v1.getHotPoint() || v2.getHotPoint()) {
            System.out.println("fuck it");
            return;
        }

        Set<CustomVertex> visited = new HashSet<CustomVertex>();
        Stack<CustomVertex> stack = new Stack<CustomVertex>();
        List<List<CustomVertex>> pathList = new LinkedList<List<CustomVertex>>();
        Set<CustomVertex> hotpoints = new HashSet<CustomVertex>();
        findAllSimplePathHP(v1, v2, graph, visited, stack, pathList, hotpoints);

        visited.clear();
        stack.clear();
        List<List<CustomVertex>> pathRevList = new LinkedList<List<CustomVertex>>();
        Set<CustomVertex> revHotpoints = new HashSet<CustomVertex>();
        findAllSimplePathHP(v2, v1, graphRev, visited, stack, pathRevList, revHotpoints);

        Map<CustomVertex, List<List<CustomVertex>>> fromHP = new HashMap<>();
        Map<CustomVertex, List<List<CustomVertex>>> toHP = new HashMap<>();

        for(CustomVertex v : hotpoints) {
            visited.clear();
            stack.clear();
            List<List<CustomVertex>> pathTempList = new LinkedList<List<CustomVertex>>();
            findAllSimplePath(v1, v, graph, visited, stack, pathTempList);
            fromHP.put(v,pathTempList);
        }

        for(CustomVertex v : revHotpoints) {
            visited.clear();
            stack.clear();
            List<List<CustomVertex>> pathTempList = new LinkedList<List<CustomVertex>>();
            findAllSimplePath(v2, v, graphRev, visited, stack, pathTempList);
            toHP.put(v,pathTempList);
        }

        System.out.println(v1);
        System.out.println(v2);

        System.out.println("--------------");

        System.out.println(hotpoints.size());
        System.out.println(revHotpoints.size());

        System.out.println("--------------");
        for (CustomVertex v : hotpoints) {
            System.out.println(v);
        }
        System.out.println("--------------");
        for (CustomVertex v : revHotpoints) {
            System.out.println(v);
        }
        System.out.println("--------------");

        Set<CustomVertex> single = new HashSet<>();

        for (CustomVertex v : hotpoints) {
            if (revHotpoints.contains(v)) {
                single.add(v);
            }
        }

        System.out.println("--------------");
        for (CustomVertex v : single) {

            for (List<CustomVertex> from : fromHP.get(v)) {
                for (List<CustomVertex> to : toHP.get(v)) {
                    String path = "";
                    for (int i = 0; i < from.size()-1; i++) {
                        path += " " + from.get(i);
                    }
                    path += " " + v;
                    for (int i = to.size()-2; i >= 0; i--) {
                        path += " " + to.get(i);
                    }
                    System.out.println(path);
                }
            }

        }

        System.out.println("Madness");

        for (CustomVertex fhp : hotpoints) {
            for (CustomVertex thp : revHotpoints) {
                if (!fhp.equals(thp)) {
                    for (List<CustomVertex> from : fromHP.get(fhp)) {
                        for (List<CustomVertex> hpath : graphHotpoint.getAllEdges(fhp, thp).stream().map(x -> x.path).collect(Collectors.toList())) {
                            for (List<CustomVertex> to : toHP.get(thp)) {
                                String path = "";
                                for (int i = 0; i < from.size()-1; i++) {
                                    path += " " + from.get(i);
                                }

                                for (CustomVertex v : hpath) {
                                    path += " " + v;
                                }

                                for (int i = to.size()-2; i >= 0; i--) {
                                    path += " " + to.get(i);
                                }
                                System.out.println(path);
                            }
                        }
                    }
                }
            }
        }

    }


    public void GraphSSystem() {
        this.dynamicChangeNext();
        // TODO: GraphS system
    }

}
