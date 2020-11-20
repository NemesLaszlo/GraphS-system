package frontend;

import backend.Simulator;
import org.jgrapht.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel {

    private JFrame frame;
    private JPanel jpMain;
    private JPanel jpGraphBasic;
    private JPanel jpGraphReverse;
    private JPanel jpGraphHotpoint;
    private JPanel jpContorls;
    private Simulator simulator;
    private GraphPrinter graphPrinter;
    private String graphType;
    private int startNodeNum = 30;
    private boolean showEdges = true;
    private boolean autoTurnedOn = false;
    private ActionListener timerAL;
    private Timer timer;

    public MainPanel() {
        frame = new JFrame();
        jpMain = new JPanel();
        jpGraphBasic = new JPanel();
        jpGraphReverse = new JPanel();
        jpGraphHotpoint = new JPanel();
        jpContorls = new JPanel();
        simulator = new Simulator(startNodeNum);
        graphPrinter = new GraphPrinter(simulator.getGraph());
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
                simulator.setActualTimeStamp(0);
                simulator.createDynamicGraph(newValInteger);
                graphPrinter.setG(simulator.getGraph());
                //graphPrinter.SimulationInit(simulator, startNodeNum);
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
//        jpMain.add(jpGraph);

        JTabbedPane tabbedPane = new JTabbedPane();
//        jpGraph.setPreferredSize(new Dimension(400,400));
//        tabbedPane.setBounds(0,0,400,400);
        tabbedPane.addTab("Basic", jpGraphBasic);
        tabbedPane.addTab("Reverse", jpGraphReverse);
        tabbedPane.addTab("Hotpoint", jpGraphHotpoint);

        jpMain.add(tabbedPane);
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

    private void genGraphPrinter(Graph g, JPanel panel) {
        graphPrinter = new GraphPrinter(g);
        graphPrinter.regenerateGraphVisual();

        graphPrinter.repaint();
        graphPrinter.getContentPane().repaint();

        panel.removeAll();
        panel.add(graphPrinter);

        panel.repaint();
        panel.revalidate();
    }

    private void rePaintGraph(){

        genGraphPrinter(simulator.getGraph(), jpGraphBasic);
        genGraphPrinter(simulator.getGraphRev(), jpGraphReverse);
        genGraphPrinter(simulator.getGraphHotpoint(), jpGraphHotpoint);


        jpMain.repaint();
        jpMain.revalidate();

        frame.repaint();
        frame.pack();

        graphPrinter.enableEdges(showEdges);
    }
}
