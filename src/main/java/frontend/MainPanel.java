package frontend;

import backend.Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel {

    private JFrame frame;
    private JPanel jpMain;
    private JPanel jpGraph;
    private JPanel jpContorls;
    private Simulator simulator;
    private GraphPrinter graphPrinter;
    private String graphType;
    private int startNodeNum = 7;
    private boolean showEdges = true;
    private boolean autoTurnedOn = false;
    private ActionListener timerAL;
    private Timer timer;

    public MainPanel() {
        frame = new JFrame();
        jpMain = new JPanel();
        jpGraph = new JPanel();
        jpContorls = new JPanel();
        simulator = new Simulator();
        graphPrinter = new GraphPrinter();
    }

    public void interfaceVisualization() {

        graphPrinter.SimulationInit(simulator, startNodeNum);
        rePaintGraph();

        // Node input
        SpinnerModel nodesModel = new SpinnerNumberModel(startNodeNum, 1, 100, 1);
        JSpinner nodesSpinner = new JSpinner(nodesModel);
        nodesSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                JSpinner s = (JSpinner) e.getSource();
                String newValString = s.getValue().toString();
                int newValInteger = Integer.parseInt(newValString);

                startNodeNum = newValInteger;
                graphPrinter.SimulationInit(simulator, startNodeNum);
                rePaintGraph();
            }
        });

        // Next button
        JButton next = new JButton("Next");
        next.setBounds(50,100,95,30);
        next.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                simulator.GraphSSystem();
                rePaintGraph();
            }
        });

        timerAL = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                simulator.GraphSSystem();
                rePaintGraph();
            }
        };

        // Auto button
        JButton auto = new JButton("Auto");
        auto.setBounds(50,100,95,30);
        auto.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(!autoTurnedOn) {
                    timer = new Timer(1000,timerAL);
                    timer.setRepeats(true);
                    timer.start();
                    autoTurnedOn = true;
                }
            }
        });

        // Stop button
        JButton stop = new JButton("Stop");
        stop.setBounds(50,100,95,30);
        stop.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(autoTurnedOn){
                    timer.stop();
                    timer = null;
                    autoTurnedOn = false;
                }
            }
        });

        // Enable edges
        JCheckBox edges = new JCheckBox("Show edges");
        edges.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent e) {
                showEdges = e.getStateChange() == java.awt.event.ItemEvent.SELECTED ? true : false;
                graphPrinter.enableEdges(showEdges);
            }
        });
        edges.setSelected(showEdges);

        // Graph
        jpMain.add(jpGraph);

        //Contorls
        jpContorls.add(new Label("Nodes count:"));
        jpContorls.add(nodesSpinner);
        jpContorls.add(next);
        jpContorls.add(auto);
        jpContorls.add(stop);
        jpContorls.add(edges);

        jpContorls.setLayout(new BoxLayout(jpContorls, BoxLayout.Y_AXIS));
        jpMain.add(jpContorls);

        frame.add(jpMain);
        frame.setTitle("GraphS System Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void rePaintGraph(){

        graphPrinter = new GraphPrinter();
        graphPrinter.regenerateGraphVisual(simulator);

        graphPrinter.repaint();
        graphPrinter.getContentPane().repaint();

        jpGraph.removeAll();
        jpGraph.add(graphPrinter);

        frame.repaint();

        jpMain.repaint();
        jpMain.revalidate();

        jpGraph.repaint();
        jpGraph.revalidate();

        frame.pack();

        graphPrinter.enableEdges(showEdges);
    }
}
