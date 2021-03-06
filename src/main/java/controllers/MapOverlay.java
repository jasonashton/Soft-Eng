package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Griffin on 4/5/2017.
 */
public class MapOverlay {
    public MapOverlay(Pane scenePane , mapScene controller){
        this.currentPane = scenePane;
        System.out.println("map   " + currentPane );
        this.sceneController = controller;
    }

    private Pane currentPane;
    private HashMap<Integer, controllers.Node> currentNodeMap;
    private Line lne;
    private Circle location;
    private static final double labelRadius = 6.8;
    private static final double labelTypeRadius = 15;
    private final double sizeUpRatio = 1.9;

    private final double lineHighlightedStrokeW = 4.5;
    private final double lineNonHighlightedStrokeW = 1.7;
    private final double lineSizeUpRatio  = 1.5;
    mapScene sceneController;

    private ArrayList<Circle> ButtonList = new ArrayList<Circle>();

    private ArrayList<Line> lineList = new ArrayList<Line>();

    private static double zoom = 1;
    private static double heightRatio = 1;
    private static double widthRatio = 1;

    private static int pathfinding = 0;

    //takes in a Hashtable when scene is switched and calls setNodes

    public void setMapAndNodes(HashMap<Integer, Node> nodeMap, boolean devMode, int floor, int permissionLevel) {

        if (devMode) {
            System.out.println("DEVMODE active");
        }

        currentNodeMap = nodeMap;
        // Clear circles from the scene
        while (ButtonList.size() > 0) {
            currentPane.getChildren().remove(ButtonList.get(0));
            ButtonList.remove(0);
        }
        boolean isStair = false;
        // Add all the nodes onto the scene as buttons
        for (controllers.Node current : nodeMap.values()) {

                //  - node can be disabled and show in dev mode
                //devs can see everything and interact with everything
                if (devMode == true) {

                    create_Button(current.getPosX(), current.getPosY(), current.getIsHidden(), current.getEnabled(), floor, devMode, current.getType());

                } else {
                    //if not dev mode:
                    //show only if enabled and not hidden
                    if (current.getIsHidden() == false && current.getEnabled() == true && permissionLevel >= current.getPermissionLevel()) {
                        create_Button(current.getPosX(), current.getPosY(), false, true, floor, devMode, current.getType());
                    }
                }
                //else skip displaying the node

            }
            wipeEdgeLines();
        }

    final Image stairImage = new Image("images/stairsImage.png");
    final Image elevatorImage = new Image("images/elevator.png");
    final Image foodImage = new Image("images/ coffee.png");
    final Image entranceImage = new Image("images/entrance.png");
    final Image exitImage = new Image("images/exit.jpg");
    final Image officeImage = new Image("images/office.png");
    final Image restroomImage = new Image("images/restroom.png");
    final Image kioskImage = new Image("images/kiosk.png");



    public void create_Button(int nodeX, int nodeY, boolean hidden, boolean enabled, int floor, boolean devmode, String type){
        //System.out.println("checking button");
        //System.out.println("make button");

        Node current = MapController.getInstance().getCollectionOfNodes().getNode(nodeX, nodeY, floor);
        final String infoString;
        //have less info presented to the visitors
        if (devmode) {
            infoString = "x: " + nodeX + " y: " + nodeY + " Floor: " + floor + "\n" +
                    "Name: " + current.getName() + "\n" +
                    "Room: " + current.getRoomNum() + "\n" +
                    "Type: " + current.getType() + "\n" +
                    "Permission Level: " + current.getPermissionLevel();
        } else {
            infoString = "Name: " + current.getName() + "\n" +
                    "Room: " + current.getRoomNum() + "\n" +
                    "Type: " + current.getType() + "\n" +
                    "Floor: " + floor;
        }


            location = new Circle(labelRadius);
            switch (current.getType()) {
                case "Stair":
                    location.setFill(new ImagePattern(stairImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Elevator":
                    location.setFill(new ImagePattern(elevatorImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Doctor's Office":
                    location.setFill(new ImagePattern(officeImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Food Service":
                    location.setFill(new ImagePattern(foodImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Exit":
                    location.setFill(new ImagePattern(exitImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Restroom":
                    location.setFill(new ImagePattern(restroomImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Entrance":
                    location.setFill(new ImagePattern(entranceImage));
                    location.setRadius(labelTypeRadius);
                    break;
                case "Kiosk":
                    location.setFill(new ImagePattern(kioskImage));
                    location.setRadius(25);
                    break;
            }
            //System.out.println("Name of the current kiosk: " + current.getName());
            if (current.getName().equals("Kiosk")){
                location.setFill(new ImagePattern(kioskImage));
                location.setRadius(25);
            }
            location.setOnMouseClicked(e -> {
                Object o = e.getSource();
                Circle c = (Circle) o;

                //only work for left click
                if (e.getButton() == MouseButton.PRIMARY) {
                    sceneController.sceneEvent((int)((nodeX)), (int)((nodeY)), c);
                }

            });

        location.setOnMouseEntered(e -> {
            Object o = e.getSource();
            Circle c = (Circle) o;
            c.setRadius(labelRadius * sizeUpRatio);
            switch (current.getType()) {
                case "Stair":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Elevator":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Doctor's Office":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Food Service":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Exit":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Restroom":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Entrance":
                    c.setRadius(labelTypeRadius*1.4);
                    break;
                case "Kiosk":
                    c.setRadius(25*1.4);
                    break;
            }
            if (current.getName().equals("Kiosk")){
                c.setRadius(30);
            }
            Tooltip.install(
                    c,
                    new Tooltip(infoString)
            );

        });
        location.setOnMouseExited(e -> {
            Object o = e.getSource();
            Circle c = (Circle) o;
            c.setRadius(labelRadius);
            switch (current.getType()) {
                case "Stair":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Elevator":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Doctor's Office":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Food Service":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Exit":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Restroom":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Entrance":
                    c.setRadius(labelTypeRadius);
                    break;
                case "Kiosk":
                    c.setRadius(25);
                    break;
            }
            if (current.getName().equals("Kiosk")){
                c.setRadius(25);
            }
        });

        // this code sets node's x and y pos to be on the plane holding the graph
        currentPane.getChildren().add(location);
        location.setLayoutX(round(nodeX * zoom * widthRatio));
        location.setLayoutY(round(nodeY * zoom * heightRatio));
        location.toFront();

        if (!enabled) {
            location.setFill(Color.RED);
        } else if(hidden) {
            location.setFill(Color.GRAY);
        }

        if (devmode) {
            location.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        Object o = event.getSource();
                        Circle c = (Circle) o;

                        // Create ContextMenu
                        ContextMenu contextMenu = new ContextMenu();
                        contextMenu.setImpl_showRelativeToWindow(true);
                        MenuItem removeOption = new MenuItem("Remove");
                        removeOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                currentPane.getChildren().remove(c);
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 1);
                            }
                        });
                        MenuItem editOption = new MenuItem("Edit Information");
                        editOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 2);
                            }
                        });
                        MenuItem autoGenEdgeOption = new MenuItem("Autogenerate Edges");
                        autoGenEdgeOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 3);
                            }
                        });
                        MenuItem addEdgeOption = new MenuItem("Add Single Edge");
                        addEdgeOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 4);
                            }
                        });
                        MenuItem addMultiEdgeOption = new MenuItem("Add Multiple Edges");
                        addMultiEdgeOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 5);
                            }
                        });
                        MenuItem removeAllEdgeOption = new MenuItem("Remove All Edges");
                        removeAllEdgeOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 6);
                            }
                        });
                        MenuItem editPositionOption = new MenuItem("Edit Position");
                        editPositionOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 7);
                            }
                        });
                        MenuItem toggleEnabled = new MenuItem("Toggle Node Enabled");
                        toggleEnabled.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 9);
                            }
                        });
                        MenuItem toggleHidden = new MenuItem("Toggle Node Hidden");
                        toggleHidden.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.rightClickEvent((int)((nodeX)), (int)((nodeY)), c, 10);
                            }
                        });
                        if (current.getType().equalsIgnoreCase("Elevator") ||
                                current.getType().equalsIgnoreCase("Stair")) {
                            MenuItem editFloorsConnectedTo = new MenuItem("Show Connected Floors");
                            editFloorsConnectedTo.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent e) {
                                    sceneController.rightClickEvent((int) ((nodeX)), (int) ((nodeY)), c, 8);
                                }
                            });
                            contextMenu.getItems().addAll(removeOption, editOption, editPositionOption, toggleEnabled, toggleHidden, autoGenEdgeOption,
                                    addEdgeOption, addMultiEdgeOption, removeAllEdgeOption, editFloorsConnectedTo);
                        } else {
                            contextMenu.getItems().addAll(removeOption, editOption, editPositionOption, toggleEnabled, toggleHidden, autoGenEdgeOption,
                                    addEdgeOption, addMultiEdgeOption, removeAllEdgeOption);
                        }
                        contextMenu.show(location, event.getScreenX(), event.getScreenY());
                        }
                }
            });
        }
        ButtonList.add(location);
    }


    public void wipeEdgeLines(){
        for(Line currentLine : lineList) {
            currentPane.getChildren().remove(currentLine);
        }

    }
    //creates visual representations of the edges of nodes on the pane
    //  input: any arraylist of Edge objects
    //NOTE: caller is responsible for not sending duplicate edges
    public void createEdgeLines(ArrayList<controllers.Edge> edgeList, boolean highlighted, boolean devmode) {
        //for-each loop through arraylist
        wipeEdgeLines();
        for(controllers.Edge thisEdge: edgeList) {
            lne = new Line();

            //don't draw lines if on different floor
            if (thisEdge.getStartNode().getFloor() != thisEdge.getEndNode().getFloor()) {
                continue;
                //maybe use colors???
            }

            if (devmode) {
                lne.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        Object o = e.getSource();
                        Line lne = (Line) o;
                        //sceneController.EdgeEvent(lne.getStartX(), lne.getStartY());
                        // Create ContextMenu
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem removeOption = new MenuItem("Remove Edge");
                        removeOption.setOnAction(new EventHandler<ActionEvent>() {
                            @Override public void handle(ActionEvent e) {
                                sceneController.edgeClickRemove(round(lne.getStartX()), round(lne.getStartY()),
                                        round(lne.getEndX()), round(lne.getEndY()));
                            }
                        });

                        //show menu
                        contextMenu.getItems().addAll(removeOption);
                        contextMenu.show(location, e.getScreenX(), e.getScreenY());

                    }
                });

                lne.setOnMouseEntered(e -> {
                    Object o = e.getSource();
                    Line lne = (Line) o;
                    if (highlighted) {
                        lne.setStrokeWidth(lineHighlightedStrokeW * lineSizeUpRatio);
                    } else {
                       lne.setStrokeWidth(lineNonHighlightedStrokeW * lineSizeUpRatio);
                    }
                });

                lne.setOnMouseExited(e -> {
                    Object o = e.getSource();
                    Line lne = (Line) o;
                    if (highlighted) {
                        lne.setStrokeWidth(lineHighlightedStrokeW );
                    } else {
                        lne.setStrokeWidth(lineNonHighlightedStrokeW);
                    }
                });
            }

            //config to display properly
            if(highlighted) {
                lne.setFill(Color.RED);
                lne.setStroke(Color.RED);
                lne.setStrokeWidth(lineHighlightedStrokeW);
            } else {
                lne.setStroke(Color.BLACK);
                lne.setStrokeWidth(lineNonHighlightedStrokeW);
            }

            //add to pane
            currentPane.getChildren().add(lne);
            lne.setStartX(round(thisEdge.getStartNode().getPosX() * zoom * widthRatio));
            lne.setStartY(round(thisEdge.getStartNode().getPosY() * zoom * heightRatio));
            lne.setEndX(round(thisEdge.getEndNode().getPosX() * zoom * widthRatio));
            lne.setEndY(round(thisEdge.getEndNode().getPosY() * zoom * heightRatio));
            //show
            lne.toFront();
            //add to list
            lineList.add(lne);
        }
    }

    //get method for labelRadius
    public double getLabelRadius() {
        return labelRadius;
    }

    //get method for label type radius
    public double getLabelTypeRadius() {
        return labelTypeRadius;
    }

    //get method for the circle list (buttonList)
    public ArrayList<Circle> getButtonList() {
        return ButtonList;
    }

    //draw each floor's edge lines
    public void drawFloorEdges(int floor) {
        ArrayList<Edge> currentFloorEdges = new ArrayList<>();

        //get all edges for this floor in that one.
        for (Edge e: MapController.getInstance().getEdgeCollection()) {
            if (e.getStartNode().getFloor() == floor && e.getEndNode().getFloor() == floor) {
                currentFloorEdges.add(e);
            }
        }

        createEdgeLines(currentFloorEdges, false, false);
    }

    public static double getZoom() {
        return zoom;
    }

    public static void setZoom(double zoom1) {
        zoom = zoom1;
    }

    public static int getPathfinding() {
        return pathfinding;
    }

    public static void setPathfinding(int newpath) {
        pathfinding = newpath;
    }

    public static void setHeightRatio(double ratio) {
         heightRatio = ratio;
    }

    public static double getHeightRatio() {
        return heightRatio;
    }

    public static void setWidthRatio(double ratio) {
        widthRatio = ratio;
    }

    public static double getWidthRatio() {
        return widthRatio;
    }

    private int round(double input) {
        long intPart;
        double decimalPart;
        intPart = (long) input;
        decimalPart = input - intPart;

        if (decimalPart >= 0.5d) {
            return (int) intPart + 1;
        } else {
            return (int) intPart;
        }

    }
}
