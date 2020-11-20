package frontend;

import backend.CustomEdge;
import backend.CustomVertex;
import backend.Simulator;

import com.mxgraph.layout.*;
import com.mxgraph.swing.*;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphPrinter extends JApplet {

    private final Dimension DEFAULT_SIZE = new Dimension(1280, 720);
    private JGraphXAdapter<CustomVertex, CustomEdge> jgxAdapter;
    private mxGraphComponent component;
    private Object[] cells;
    private Object[] edges;
    private Graph<CustomVertex, CustomEdge> g;

    /**
     * GraphPrinter Constructor
     */
    public GraphPrinter(Graph g){
        this.g = g;
    }

    public void setG(Graph<CustomVertex, CustomEdge> g) {
        this.g = g;
    }

    /**
     * Visualization init of the simulation with the graph and basic display of the start condition.
     * @param simulator - Simulator object with the simulation methods.
     * @param numberOfNodes - Number of the graph nodes.
     */
    public void SimulationInit(Simulator simulator, int numberOfNodes)
    {
        //simulator.createDynamicGraph(numberOfNodes);
//        regenerateGraphVisual(simulator);
    }

    /**
     * Basic graph visualization with coloring and positioning.
     */
    void regenerateGraphVisual() {
        // create a visualization using JGraph, via an adapter

        jgxAdapter = new JGraphXAdapter<CustomVertex, CustomEdge>(g);
        component = new mxGraphComponent(jgxAdapter);

        jgxAdapter.refresh();


        // get all cells and edges
        cells = jgxAdapter.getChildVertices(jgxAdapter.getDefaultParent());
        edges = component.getGraph().getAllEdges(cells);

        // Coloring
        jgxAdapter.setCellStyles(com.mxgraph.util.mxConstants.STYLE_FILLCOLOR,"white",cells);

        //Enable,disable edge labels
        jgxAdapter.setCellStyles(com.mxgraph.util.mxConstants.STYLE_NOLABEL,"1", edges);

        component.setConnectable(false);
        component.getGraph().setAllowDanglingEdges(false);
        getContentPane().add(component);
        resize(DEFAULT_SIZE);

        // positioning via jgraphx layouts
        mxCircleLayout layout = new mxCircleLayout(jgxAdapter);

        // center the circle
        int radius = 500;
        layout.setRadius(radius);
        layout.setMoveCircle(true);

        layout.execute(jgxAdapter.getDefaultParent());
    }

    /**
     * Enable or Disable the edges on the graph visualization panel.
     */
    public void enableEdges(Boolean enable){
        if(!enable)jgxAdapter.removeCells(edges);
        else jgxAdapter.addCells(edges);
    }

}
