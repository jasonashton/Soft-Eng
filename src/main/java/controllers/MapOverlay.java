package controllers;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Griffin on 4/5/2017.
 */
public class MapOverlay {
    public MapOverlay(Pane scenePane , AbsController controller){
        this.currentPane = scenePane;
        System.out.println("map   " + currentPane );
        this.sceneController = controller;
    }

    private Pane currentPane;
    private HashMap<Integer, controllers.Node> currentNodeMap;
    private Line lne;
    private Circle location;
    private static final double lableRadius = 8.5;
    AbsController sceneController;

    private ArrayList<Circle> ButtonList = new ArrayList<Circle>();

    private ArrayList<Line> lineList = new ArrayList<Line>();

    //takes in a Hashtable when scene is switched and calls setNodes
    public void setMapAndNodes(HashMap<Integer, Node> nodeMap) {
        currentNodeMap = nodeMap;

        // Clear circles from the scene
        while (ButtonList.size() < 0) {
            currentPane.getChildren().remove(ButtonList.get(0));
            ButtonList.remove(0);
        }

        // Add all the nodes onto the scene as buttons
        for(controllers.Node current: nodeMap.values()){

            //criteria for node to display:
            //  - node must be enabled
            //  - node must not be hidden
            if (current.getIsHidden() == false && current.getEnabled() == true) {
                create_Button(current.getPosX(), current.getPosY());
            }
            //else skip displaying the node
        }
    }

    public void create_Button(double nodeX, double nodeY){
        //System.out.println("checking button");
        //System.out.println("make button");
        location = new Circle(lableRadius);
        location.setOnMouseClicked(e -> {

            Object o = e.getSource();
            Circle c = (Circle) o;


            sceneController.sceneEvent((int)((nodeX)), (int)((nodeY)), c);
            //set color --

//            if (selectionState == 0) {
//                c.setFill(Color.MAGENTA);
//            } else if (selectionState == 1) {
//                c.setFill(Color.GREEN);
//            } else {
//                //do nothing
//            }
        });

        // this code sets node's x and y pos to be on the plane holding the graph
        currentPane.getChildren().add(location);
        location.setLayoutX(nodeX);
        location.setLayoutY(nodeY);
        location.toFront();

        ButtonList.add(location);
    }
    //creates visual representations of the edges of nodes on the pane
    //  input: any arraylist of Edge objects
    //NOTE: caller is responsible for not sending duplicate edges
    public void createEdgeLines(ArrayList<controllers.Edge> edgeList) {
        //for-each loop through arraylist
        for(Line currentLine : lineList) {
            currentPane.getChildren().remove(currentLine);
        }
        for(controllers.Edge thisEdge: edgeList) {
            lne = new Line();
            //add to pane
            currentPane.getChildren().add(lne);
            lne.setStartX(thisEdge.getStartNode().getPosX());
            lne.setStartY(thisEdge.getStartNode().getPosY());
            lne.setEndX(thisEdge.getEndNode().getPosX());
            lne.setEndY(thisEdge.getEndNode().getPosY());
            //show
            lne.toFront();
            //add to list
            lineList.add(lne);
        }
    }
}
