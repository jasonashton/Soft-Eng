package NewIntroUI;

import DBController.DatabaseController;
import controllers.*;
import controllers.Node;
import emergency.SmsSender;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import pathFindingMenu.Pathfinder;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by AugustoR on 4/21/17.
 */
public class NewIntroUIController extends controllers.mapScene{
    @FXML
    private Tab textDirections_Tab;

    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Label mainTitle_Label;

    @FXML
    private ScrollPane locationsPane;

    @FXML
    private ChoiceBox<String> language_ChoiceBox;

    @FXML
    private Button about_Button;

    @FXML
    private Button admin_Button;

    @FXML
    private Button emergency_Button;

    @FXML
    private Label start_Label;

    @FXML
    private TextField start_textField;

    @FXML
    private Label end_Label;

    @FXML
    private TextField end_TextField;

    @FXML
    private TextField filter_textField;

    @FXML
    private Button cancel_Button;

    @FXML
    private Button submit_Button;

    @FXML
    private Label textD_Label;

    @FXML
    private TextArea textDescription_TextFArea;

    @FXML
    private TextField phoneInsert;

    @FXML
    private Button phoneSend;

    @FXML
    private ChoiceBox<String> floor_ChoiceBox;

    @FXML
    private Label floor_Label;

    @FXML
    private Label phoneInfo_Label;

    @FXML
    private Label c_Floor_Label;

    @FXML
    private Pane node_Plane;

    @FXML
    private ImageView map_viewer;

    @FXML
    private Label phoneStatus;

    @FXML
    private Button directory_Button;

    @FXML
    private Button previous_Button;

    @FXML
    private Button continueNew_Button;

    @FXML
    private Button zoomIn_button;

    @FXML
    private Button zoomOut_button;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private StackPane mapStack;

    @FXML
    private Label LogInPerson_Label;

    @FXML
    private Button cancelTo_Button;

    @FXML
    private Button cancelFirst_Button;

    @FXML
    private Button FAQ_Button;

    @FXML
    private Button DirectoryMan_Button;

    @FXML
    private Button adminMan_Button;

    @FXML
    private Button MapMan_Button;

    @FXML
    private CheckBox ThreeDPATH_CheckBox;

    @FXML
    private CheckBox stairs_CheckBox;

    @FXML
    private Button reverse_Button;

    @FXML
    private Tab location_Tab;

    @FXML
    private Label threeD_Label;

    @FXML
    private Label stairs_Label;

    @FXML
    private Label searchError;




    int c_language = 0;

    int first_Time = 0;

    boolean second = false;

    private int currentFloor;

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    private controllers.MapOverlay graph;

    private int selectionState;

    private MapController mapController = MapController.getInstance();

    private Circle start;
    private Circle end;

    private final double sizeUpRatio = 1.9;
    private final double strokeRatio = 4;

    private ArrayList<ArrayList<Edge>> globalFragList;
    private int fragPathPos; //position on the global frag list
    private ArrayList<Integer> globalFloorSequence;
    private ArrayList<Edge> path;

//    private final Color startColor = Color.GREEN;
//    private final Color endColor = Color.CRIMSON;
    private final Color startColor = Color.CRIMSON;
    private final Color endColor = Color.GREEN;
    private final Color kioskColor = Color.ORANGE;
    private final Color interStart = Color.DARKRED;
    private final Color interEnd = Color.DARKGREEN;

    private double origPaneWidth;
    private double origPaneHeight;
    double zoom;
    double heightRatio = (1050.0/489.0);
    double widthRatio = (1600.0/920.0);

    private int permissionLevel = 0;

    double currentHval = 0;
    double currentVval = 0;

    private boolean useStairs;

    private double submitHval;
    private double submitVval;


    //ArrayList<Edge> zoomPath;

    // list of all locations, not sorted at first
    ArrayList<Location> locations = new ArrayList<>();
    // list of added room numbers to prevent duplication (MAYBE DELETE LATER)
    ArrayList<String> roomNums = new ArrayList<>();
    
    String type, name, roomNum, firstName, lastName, title;
    boolean isHidden, enabled;
    int permissions;

    boolean changeStart = false;


    @FXML
    public void initialize() {
        reverse_Button.setStyle("-fx-background-image: url('images/reverse.png')");
        graph = new controllers.MapOverlay(node_Plane, (mapScene) this);
        MapController.getInstance().requestMapCopy();
        filter_textField.setPromptText("search");

        graph.setZoom(1.0);

        //setLanguageChoices(c_language);
        setFloorChoices();
        setStartEndChoices(); // text auto fill function
        setLanguage_ChoiceBox(c_language);
        setTypeChoices();
        //setLocationsForFloor("Floor 1");
        //setComboBox();
        //setFilterChoices();
        //set current floor
        //we will use floor 1 as default
        currentFloor = 1;
        c_Floor_Label.setText("1");
        useStairs = false;

        System.out.println("width/height ratios: " + widthRatio + "/" + heightRatio);

        node_Plane.setMaxWidth(4000.0);
        node_Plane.setMaxHeight(3000.0);
        node_Plane.setPrefHeight(489.0*heightRatio);
        node_Plane.setPrefWidth(920.0*widthRatio);
        map_viewer.setFitHeight(489.0*heightRatio);
        map_viewer.setFitWidth(920.0*widthRatio);
        //map_viewer.fitWidthProperty().bind(node_Plane.widthProperty());
        //map_viewer.fitHeightProperty().bind(node_Plane.heightProperty());

        graph.setWidthRatio(widthRatio);
        graph.setHeightRatio(heightRatio);

        graph.setMapAndNodes(MapController.getInstance().getCollectionOfNodes().getMap(currentFloor), false,
                currentFloor, permissionLevel);
        //set continue button invisible when not needed
        continueNew_Button.setVisible(false);
        previous_Button.setVisible(false);
        second = true;

        changeFloor(databaseController.GetKioskFloor() - 1);
        floor_ChoiceBox.getSelectionModel().select(databaseController.GetKioskFloor() - 1);

        //draw edges
        //graph.drawFloorEdges(currentFloor);
        origPaneHeight = 489;
        origPaneWidth = 920;

        //detects scrolling for zoom while keeping scrollpane from panning with mousewheel
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            mapScroll(event);
            event.consume();
        });
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        ArrayList<Location> passLocations = grabData("");
        // sorts locations based on distance from the Kiosk
        final ArrayList<Location> sortedPassLocations = sortCloseToKiosk(passLocations, "Kiosk");
        // makes a list of all locations as clickable buttons
        setLocationsListView(sortedPassLocations);


        filter_textField.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)){
                    setLocationsListView(sortCloseToKiosk(grabData(filter_textField.getText()), start_textField.getText()));
                }
            }
        });
        start_textField.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    setLocationsListView(sortCloseToKiosk(passLocations, start_textField.getText()));
                }
            }
        });
    }

    //get an instance of database controller
    DatabaseController databaseController = DatabaseController.getInstance();

    public void drawCircleList(ArrayList<Circle> circleList, double x, double y, Color color) {
        for (Circle c : circleList) {
            //System.out.println((c.getLayoutX()/zoom)/widthRatio);
            System.out.println("layoutX/givenX " + c.getLayoutX()+"/"+x);
            if (round(c.getLayoutX()) == x && round(c.getLayoutY()) == y) {
                System.out.println("Found circle");
                c.setStrokeWidth(strokeRatio);
                c.setRadius(graph.getLabelRadius()*sizeUpRatio);
                c.setStroke(color);
                if (c.getFill().equals(kioskColor)) {
                    c.setFill(kioskColor);
                } else if (c.getFill() == Color.BLACK) {
                    c.setFill(color);
                }
                break;
            }
        }
    }

    // grabs data from database to later create the location buttons
    private ArrayList<Location> grabData(String filterType){
        ArrayList<Location> locs = new ArrayList<>();
        ResultSet rset2 = databaseController.getFilteredRoomNames2();
        try {
            while (rset2.next()){
                name = rset2.getString("NAME");
                roomNum = rset2.getString("ROOMNUM");
                type = rset2.getString("TYPE");
                permissions = rset2.getInt("PERMISSIONS");
                if (permissions == 0){
                    ResultSet rsetNode;
                    int nodeFloor = 0;
                    rsetNode = databaseController.getNodeWithName(roomNum);
                    while (rsetNode.next()){
                        nodeFloor = rsetNode.getInt("FLOOR");
                    }
                    if (filterType.equals("")){
                        locs.add(new Location(type, name, roomNum, "", "",
                                "", permissions, nodeFloor));
                    } else {
                        if (type.equals(filterType)){
                            locs.add(new Location(type, name, roomNum, "", "",
                                    "", permissions, nodeFloor));
                        }
                    }

                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        ResultSet rset = databaseController.getProRoomNums();
        try{
            while (rset.next()){
                title = rset.getString("TYPE");
                roomNum = rset.getString("ROOMNUM");
                firstName = rset.getString("FIRSTNAME");
                lastName = rset.getString("LASTNAME");
                permissions = rset.getInt("PERMISSIONS");
                if (permissions == 0){
                    ResultSet rsetNode;
                    int nodeX = 0, nodeY = 0, nodeFloor = 0, nodePermissions = 0;
                    String nodeName = null, nodeType = null, nodeRoom = null;
                    boolean nodeIsHidden = false, nodeEnabled = false;
                    rsetNode = databaseController.getNodeWithName(roomNum);
                    while (rsetNode.next()){
                        nodeFloor = rsetNode.getInt("FLOOR");
                    }
                    if (filterType.equals("")){
                        locs.add(new Location("Doctor's Office", "", roomNum, firstName, lastName,
                                title, permissions, nodeFloor));
                    } else {
                        if (filterType.equals("Doctor's Office") || filterType.equalsIgnoreCase("doctor")){
                            locs.add(new Location("Doctor's Office", "", roomNum, firstName, lastName,
                                    title, permissions, nodeFloor));
                        }
                    }

                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return locs;
    }

    private void setLocationsListView(ArrayList<Location> locs){

        locationsPane.setPadding(new Insets(10, 10, 5, 10));

        VBox root = new VBox();

        Iterator<Location> iter = locs.iterator();

        while (iter.hasNext()) {
            Location thisLocation = iter.next();
            HBox empHBox = new HBox();
            empHBox.setPadding(new Insets(2, 2, 2, 2));
            empHBox.setSpacing(2);
            Button nameButton = new Button();
            Text text;

            if (thisLocation.getAssociatedProFirst().equals("")) {
                text = new Text(thisLocation.getName() + ",\n" + thisLocation.getType() +
                        ", " + thisLocation.getRoomNum());
            }else {
                text = new Text(thisLocation.getAssociatedProFirst() + " " +
                        thisLocation.getAssociatedProLast() + ", " + thisLocation.getAssociatedProTitle()
                        + "\n" + thisLocation.getRoomNum());
                nameButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        if (start_textField.equals("")){
                            start_textField.setText(thisLocation.getRoomNum());
                        } else {
                            end_TextField.setText(thisLocation.getRoomNum());
                        }

                    }
                });
            }

            text.setWrappingWidth(240);
            nameButton.setPadding(new Insets(4, 4, 4, 4));

            nameButton.setContentDisplay(ContentDisplay.LEFT);
            nameButton.setPrefWidth(250);
            nameButton.setAlignment(Pos.CENTER_LEFT);
            nameButton.setGraphic(text);
            nameButton.setMaxWidth(250);
            nameButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if (start_textField.getText().equalsIgnoreCase("")) {
                        start_textField.setText(thisLocation.getName() + ", " + thisLocation.getRoomNum());
                    } else {
                        end_TextField.setText(thisLocation.getName() + ", " + thisLocation.getRoomNum());
                    }
                }
            });
            empHBox.getChildren().addAll(nameButton);
            root.getChildren().addAll(empHBox);
            locationsPane.setContent(root);

        }
    }

    Pathfinder pathFind = new Pathfinder();
    public ArrayList<Location> sortCloseToKiosk(ArrayList<Location> locs, String roomNumToFind){
            System.out.println("Here zero");
            ResultSet rset = databaseController.getNodeWithName(roomNumToFind);
            int xpos = 0, ypos = 0, floor = 0, permissions = 0;
            String type = "", name = "", roomNum = "";
            boolean isHidden = false, enabled = false;
            try {
                while (rset.next()) {
                    System.out.println("Here 0.1");
                    xpos = rset.getInt("XPOS");
                    ypos = rset.getInt("YPOS");
                    floor = rset.getInt("FLOOR");
                    isHidden = rset.getBoolean("ISHIDDEN");
                    enabled = rset.getBoolean("ENABLED");
                    type = rset.getString("TYPE");
                    name = rset.getString("NAME");
                    roomNum = rset.getString("ROOMNUM");
                    permissions = rset.getInt("PERMISSIONS");
                }
                databaseController.closeResultSet(rset);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Node startNode = new Node(xpos, ypos, floor, isHidden, enabled, type, name, roomNum, permissions);
            Iterator<Location> iter = locs.iterator();
            while (iter.hasNext()) {
                Location thisLocation = iter.next();
                System.out.println("Here once");
                ResultSet rset1 = databaseController.getNodeWithName(thisLocation.getRoomNum());
                try {
                    while (rset1.next()) {
                        xpos = rset1.getInt("XPOS");
                        ypos = rset1.getInt("YPOS");
                        floor = rset1.getInt("FLOOR");
                        isHidden = rset1.getBoolean("ISHIDDEN");
                        enabled = rset1.getBoolean("ENABLED");
                        type = rset1.getString("TYPE");
                        name = rset1.getString("NAME");
                        roomNum = rset1.getString("ROOMNUM");
                        permissions = rset1.getInt("PERMISSIONS");
                    }
                    databaseController.closeResultSet(rset1);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                Node secondNode = new Node(xpos, ypos, floor, isHidden, enabled, type, name, roomNum, permissions);
                thisLocation.weight = pathFind.getHueristic(startNode, secondNode);
                System.out.println("Here twice");
            }
            locs.sort(new Comparator<Location>() {
                @Override
                public int compare(Location o1, Location o2) {
                    //Add null check
                    return o1.weight < o2.weight ? -1
                            : o1.weight > o2.weight ? 1
                            : 0;
                }
            });
        return locs;
    }


    //Manage the clearing of the from text field
    public void cancelFromButton_Clicked(){
        start_textField.setText("");
    }

    //Manage the clearing tof the to text field
    public void cancelToButton_Clicked(){
        end_TextField.setText("");
    }

    //Button to reverse the from and to location
    public void reverseButton_Clicked(){
        String start =start_textField.getText();
        String end = end_TextField.getText();
        if( !(start == null || start.equals("") || end == null ||end.equals("")) ){
            start_textField.setText(end);
            end_TextField.setText(start);
            submitButton_Clicked();

        }
    }
    //Continue New Button Clicked
    public void continueNewButton_Clicked() {
        if (continueNew_Button.isVisible() == true) {
            System.out.println("continue button clicked");

            //set the previous button to be enabled
            previous_Button.setVisible(true);

            //increment b/c continue button
            fragPathPos++; //continue...

            //update currentfloor
            currentFloor = globalFloorSequence.get(fragPathPos);

            System.out.println("+++++++++++++++++++++++++++++++++++++++");
            System.out.println("current floor displayed: " + currentFloor);
            System.out.println("frag path pos updated to: " + fragPathPos);
            multifloorUpdate();
            //set the end goal color
            ArrayList<Circle> circleList;
            circleList = graph.getButtonList();

            //disable the continue button if you reach the end
            //also update the color
            if (fragPathPos == globalFragList.size() - 1) {
                continueNew_Button.setVisible(false);

                drawCircleList(circleList, round(endX*zoom*widthRatio), round(endY*zoom*heightRatio), endColor);
            } else {
                Circle targetStart = null;
                Circle targetEnd = null;
                ObservableList<javafx.scene.Node> sceneObjects = node_Plane.getChildren();
                Node n = findStartFromEdgeList(globalFragList.get(fragPathPos));
                for (javafx.scene.Node obj: sceneObjects) {
                    if (obj instanceof Circle) {
                        if (round((obj.getLayoutX()/zoom)/widthRatio) == n.getPosX() &&
                                round((obj.getLayoutY()/zoom)/heightRatio) == n.getPosY()) {
                            System.out.println("FOUND MY CIRCLE!!");
                            targetStart = (Circle) obj;
                        }
                    }
                }

                Node m = findEndFromEdgeList(globalFragList.get(fragPathPos));
                for (javafx.scene.Node obj: sceneObjects) {
                    if (obj instanceof Circle) {
                        if (round((obj.getLayoutX()/zoom)/widthRatio) == m.getPosX() &&
                                round((obj.getLayoutY()/zoom)/heightRatio) == m.getPosY()) {
                            System.out.println("FOUND MY CIRCLE!!");
                            targetEnd = (Circle) obj;
                        }
                    }
                }
                if (targetStart != null && targetEnd != null) {
                    drawCircleList(circleList, targetStart.getLayoutX(), targetStart.getLayoutY(), interStart);
                    drawCircleList(circleList, targetEnd.getLayoutX(), targetEnd.getLayoutY(), interEnd);
                }
            }
        }
    }

    //previous Button clicked
    public void previousButton_Clicked() {
        System.out.println("prev button clicked");

        //show the continue button
        continueNew_Button.setVisible(true);

        //decrement frag path pos
        fragPathPos--;

        //update currentfloor
        currentFloor = globalFloorSequence.get(fragPathPos);

        multifloorUpdate();
        ArrayList<Circle> circleList;
        circleList = graph.getButtonList();
        //disable the previous button if you reach the beginning
        //also update the color
        if (fragPathPos == 0) {
            previous_Button.setVisible(false);

            //set the end goal color

            drawCircleList(circleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
        } else {
            Circle targetStart = null;
            Circle targetEnd = null;
            ObservableList<javafx.scene.Node> sceneObjects = node_Plane.getChildren();
            Node n = findStartFromEdgeList(globalFragList.get(fragPathPos));
            for (javafx.scene.Node obj: sceneObjects) {
                if (obj instanceof Circle) {
                    if (round((obj.getLayoutX()/zoom)/widthRatio) == n.getPosX() &&
                            round((obj.getLayoutY()/zoom)/heightRatio) == n.getPosY()) {
                        System.out.println("FOUND MY CIRCLE!!");
                        targetStart = (Circle) obj;
                    }
                }
            }
            Node m = findEndFromEdgeList(globalFragList.get(fragPathPos));
            for (javafx.scene.Node obj: sceneObjects) {
                if (obj instanceof Circle) {
                    if (round((obj.getLayoutX()/zoom)/widthRatio) == m.getPosX() &&
                            round((obj.getLayoutY()/zoom)/heightRatio) == m.getPosY()) {
                        System.out.println("FOUND MY CIRCLE!!");
                        targetEnd = (Circle) obj;
                    }
                }
            }
            if (targetStart != null && targetEnd != null) {
                drawCircleList(circleList, targetStart.getLayoutX(), targetStart.getLayoutY(), interStart);
                drawCircleList(circleList, targetEnd.getLayoutX(), targetEnd.getLayoutY(), interEnd);
            }
        }

    }

    //Sets the choices for the language
    public void setLanguage_ChoiceBox(int lang) {
        //Makes sure you only set the choices once
        //sets the choices and sets the current language as the top choice
        if(second) {
            language_ChoiceBox.getItems().remove(0, 2);
            language_ChoiceBox.getItems().addAll("English", "Espanol");
            language_ChoiceBox.getSelectionModel().select(lang);
        }else{
            language_ChoiceBox.getItems().addAll("English", "Espanol");
            language_ChoiceBox.getSelectionModel().select(lang);

        }
        language_ChoiceBox.setTooltip(new Tooltip("Select the language"));

        //Checks if the user has decided to change languages
        language_ChoiceBox.getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        //System.out.println(newValue);

                        //Checks if the user wants english language
                        if (newValue.intValue() == 0) {
                            //Load English View
                            System.out.println("languages");
                            englishButtons_Labels();

                        } else if (newValue.intValue() == 1) {
                            //Load Spanish View
                            spanishButtons_Labels();

                        }
                    }

                });

    }

    //Changes the floors on the map
    public void changeFloor(Number newValue) {
        boolean outside = false;
        String currentF = "";
        //Print the floors accordingly
        //CODE HERE!!!!!!!

        if (newValue.intValue() == 7) {
            //outside
            currentFloor = 0;
        } else if(newValue.intValue() > 7) {
            currentFloor = newValue.intValue();
        } else {
            currentFloor = newValue.intValue() + 1;
        }
        System.out.println("currentfloor updated to: " + currentFloor);

        if (currentFloor == 0) {
            System.out.println("outside");
            outside = true;
            if (c_language == 0) {
                currentF = "Outside";
            } else {
                currentF = "Afuera";
            }
        }

        if (currentFloor == 8) {
            //outside
            outside = true;
            currentF = "Belkin 1";

        } else if (currentFloor == 9) {
            //belkin
            outside = true;
            currentF = "Belkin 2";

        } else if (currentFloor == 10) {
            outside = true;
            currentF = "Belkin 3";

        } else if (currentFloor == 11) {
            outside = true;
            currentF = "Belkin 4";

        } else if (currentFloor == 12) {
            outside = true;
            if (c_language == 0) {
                currentF = "Belkin Basement";
            } else {
                currentF = "Sotano de Belkin";
            }
        }


        mapImage newMapImage = new proxyMap(currentFloor);
        newMapImage.display(map_viewer);

        if (!outside) {
            c_Floor_Label.setText(Integer.toString(currentFloor));
            if (c_language == 0) {
                floor_Label.setText("Floor");
            } else {
                floor_Label.setText("Piso");
            }
        } else {
            c_Floor_Label.setText("");
            floor_Label.setText(currentF);
        }
        graph.setMapAndNodes(MapController.getInstance().getCollectionOfNodes().getMap(currentFloor), false,
                currentFloor, permissionLevel);

    }


    //Sets the map of the desired floor
    public void setFloorChoices(){

        //Makes sure you only set the choices once
        //sets the choices and sets the current language as the top choice
        if(c_language == 0) {
            if(second) {
                floor_ChoiceBox.getItems().remove(0,13);
                floor_ChoiceBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "Outside",
                        "Belkin 1", "Belkin 2", "Belkin 3", "Belkin 4", "Belkin Basement");
                floor_ChoiceBox.getSelectionModel().select(0);
            }else{
                floor_ChoiceBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "Outside",
                        "Belkin 1", "Belkin 2", "Belkin 3", "Belkin 4", "Belkin Basement");
                floor_ChoiceBox.getSelectionModel().select(0);
            }
        }else if(c_language == 1){
            if(second) {
                floor_ChoiceBox.getItems().remove(0,13);
                floor_ChoiceBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "Afuera",
                        "Belkin 1", "Belkin 2", "Belkin 3", "Belkin 4", "Sotano de Belkin");
                floor_ChoiceBox.getSelectionModel().select(0);
            }else{
                floor_ChoiceBox.getItems().addAll("1", "2", "3", "4", "5", "6", "7", "Afuera",
                        "Belkin 1", "Belkin 2", "Belkin 3", "Belkin 4", "Sotano de Belkin");
                floor_ChoiceBox.getSelectionModel().select(0);
            }

        }

        floor_ChoiceBox.getSelectionModel().select(0);
        map_viewer.setImage(new Image("/images/cleaned1.png"));
        floor_ChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                changeFloor(newValue);
            }
        });

    }

    public void setTypeChoices(){
        ArrayList<String> services = new ArrayList<>();
        ArrayList<String> all = new ArrayList<>();

        services = databaseController.getNodeTypes();
        all.addAll(services);
        all.add("Doctor");

        TextFields.bindAutoCompletion(filter_textField, all);
    }

    public void setStartEndChoices(){

        ArrayList<String> roomNums = new ArrayList<>();
        ArrayList<String> professionals = new ArrayList<>();
        ArrayList<String> services = new ArrayList<>();
        ArrayList<String> all = new ArrayList<>();
        System.out.println("Permission in autobind: " + permissionLevel);
        roomNums = databaseController.getFilteredRoomList(permissionLevel);
        professionals = databaseController.getProfessionalList();

        services = databaseController.getNodeTypes();
        all.addAll(roomNums);
        all.addAll(databaseController.getFilteredRooms(permissionLevel));
        //all.addAll(services);

        all.addAll(professionals);


        TextFields.bindAutoCompletion(start_textField, all);
        TextFields.bindAutoCompletion(end_TextField, all);

        start_textField.setText("Kiosk");
        selectionState = 0;
    }

    //Sends the user to the about page
    public void aboutButton_clicked() {
        FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/aboutPageView.fxml");
        aboutPage.aboutPageController controller = loader.getController();
        controller.//sets the current language given information form other screens
        setCurrentLanguage(c_language);
        if(c_language == 0){
            controller.englishLabels();
        }else{
            controller.spanishLabels();
        }
        controller.setPermissionLevel(getPermissionLevel());
        controller.setAdmin(LogInPerson_Label.getText());

    }


    //Handles the action when the submit button is clicked
    public void submitButton_Clicked() {
        Node startN;
        Node endN;
        System.out.println("submit button clicked");
        System.out.println("click - pane-Hval = " + scrollPane.getHvalue());
        System.out.println("click - pane-Vval = " + scrollPane.getVvalue());
        zoom = graph.getZoom();

        useStairs = stairs_CheckBox.isSelected();

        //reset visibility just in case
        continueNew_Button.setVisible(false);
        previous_Button.setVisible(false);

        //check that the txt fields are filled
        if(!(start_textField.getText().equals("")) && !(end_TextField.getText().equals(""))) {
            if (start_textField.getText().equals("Kiosk")){
                startN = mapController.getCollectionOfNodes().getNodeWithName("Kiosk");
            } else if (start_textField.getText().contains(",")){
                startN = mapController.getCollectionOfNodes().getNodeWithName(start_textField.getText().split(", ")[1]);
            } else {
                startN = mapController.getCollectionOfNodes().getNodeWithName(start_textField.getText());
            }

            if (end_TextField.getText().contains(",")){
                endN = mapController.getCollectionOfNodes().getNodeWithName(end_TextField.getText().split(", ")[1]);
            } else {
                endN = mapController.getCollectionOfNodes().getNodeWithName(end_TextField.getText());
            }

            //set up for colors :)
            startX = startN.getPosX();
            startY = startN.getPosY();
            endX = endN.getPosX();
            endY = endN.getPosY();

            //mark the nodes
            MapController.getInstance().markNode(startN.getPosX(), startN.getPosY(), 1, startN.getFloor());
            MapController.getInstance().markNode(endN.getPosX(), endN.getPosY(), 2, endN.getFloor());

            //detect multiflooring
            if (startN.getFloor() != endN.getFloor()) {
                //multifloor pathfinding detected
                System.out.println("directory -> multifloor pathfinding");


                multiFloorPathfind();
//                setMapToPath();
//                graph.setMapAndNodes(MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),false,
//                        currentFloor, permissionLevel);
//                graph.createEdgeLines(path, true, false);

            } else {
                //no multifloor pathfinding (simple)

                MapController.getInstance().getCollectionOfNodes().resetForPathfinding();
                path = mapController.requestPath(permissionLevel, useStairs);

                if(path.size() == 0){
                    System.out.println("Cannot find path in simple pathfinding");
                    searchError.setText("Cannot find path. Please select a different location.");
                }else{
                    searchError.setText("");
                }

                int startfloor = mapController.returnOriginalFloor();
                if(startfloor != currentFloor) {
                    c_Floor_Label.setText(Integer.toString(startfloor));

                    //switch back to the original floor using the choicebox selection
                    if (startfloor == 0) {
                        floor_ChoiceBox.getSelectionModel().select(7);
                    } else if (startfloor > 7) {
                        floor_ChoiceBox.getSelectionModel().select(startfloor);

                    } else {
                        floor_ChoiceBox.getSelectionModel().select(startfloor - 1);
                    }
                }

                graph.setPathfinding(1);
                textDescription_TextFArea.setText(mapController.getTextDirections(path, c_language));
                setMapToPath(startX, startY, endX, endY);
                graph.setMapAndNodes(MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),false,
                        currentFloor, permissionLevel);
                graph.createEdgeLines(path, true, false);
                ArrayList<Circle> circleList;
                circleList = graph.getButtonList();


                drawCircleList(circleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
                drawCircleList(circleList, round(endX*zoom*widthRatio), round(endY*zoom*heightRatio), endColor);

            }

            if(ThreeDPATH_CheckBox.isSelected()){
                //mapController.nodeListToText(pathfinder.getNodePath());
                try {
                    String os = System.getProperty("os.name");

                    if (os != null && os.equals("Mac OS X")) {
                        System.out.println("Mac OS X not supported in 3D");
                    }
                    else {
                        Runtime.getRuntime().exec(new String[]{"pathfinder3D.exe"});
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }




        System.out.println("finished - pane-Hval = " + scrollPane.getHvalue());
        System.out.println("finished - pane-Vval = " + scrollPane.getVvalue());
    }

    //Allows the user to go through several floors while using pathfinding
    public void multiFloorPathfind() {
        //initialize reference of the global frag list to null (set up)
        globalFragList = null;
        globalFloorSequence = null;
        fragPathPos = 0;

        //set continue button visible
        continueNew_Button.setVisible(true);

        //switch floors to original floor's pathfinding view
        int startfloor = mapController.returnOriginalFloor();
        c_Floor_Label.setText(Integer.toString(startfloor));

        //switch back to the original floor using the choicebox selection
        if (startfloor == 0) {
            floor_ChoiceBox.getSelectionModel().select(7);
        } else if (startfloor > 7) {
            floor_ChoiceBox.getSelectionModel().select(startfloor);

        } else {
            floor_ChoiceBox.getSelectionModel().select(startfloor - 1);
        }

        //maintain consistency of colors
        ArrayList<Circle> tempCircleList;
        tempCircleList = graph.getButtonList();

        drawCircleList(tempCircleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
        System.out.println("start coords: "+startX + "  " +startY);


        MapController.getInstance().getCollectionOfNodes().resetForPathfinding();
        ArrayList<Edge> reqPath = mapController.requestPath(permissionLevel, useStairs);
        if (reqPath == null || reqPath.size() == 0) { //can't find path, reset
            System.out.println("Could not pathfind. Resetting now...");
            cancelButton_Clicked();
            start_textField.setText("Kiosk");
        } else {
            System.out.println("reqpath size" + reqPath.size());
            textDescription_TextFArea.setText(mapController.getTextDirections(reqPath, c_language));

            ArrayList<ArrayList<Edge>> fragPath;
            fragPath = mapController.requestFragmentedPath(reqPath, mapController.returnOriginalFloor(), mapController.returnDestFloor());

            //loop and display the edges per floor - use the startfloor


            if (fragPath.get(0).size() == 0) {
                //only occurs if the first transition is a null
                //instead just highlight the first thing

                //todo -> highlight

            } else {
                graph.createEdgeLines(fragPath.get(0), true, false);
                graph.setPathfinding(2);
                setMapMulti(fragPath.get(0));
            }

            //set the globals so you can send to the continue button
            globalFragList = fragPath;
            globalFloorSequence = mapController.getFloorSequence();

            //print floor sequence (testing)
            for (int i = 0; i < globalFloorSequence.size(); i++) {
                System.out.println(globalFloorSequence.get(i));
            }
        }


    }

    //Handling when the logIn Button is clicked
    public void logInButton_Clicked() {

        graph.setZoom(1.0);
        graph.setHeightRatio(1.0);
        graph.setWidthRatio(1.0);

        if(getPermissionLevel() == 0 ) {
            FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/adminLoginMainView.fxml");
            adminLoginMain.adminLoginMainController controller = loader.getController();
            //sends the current language the next screen
            controller.setC_language(c_language);
            //set up english labels
            if (c_language == 0) {
                controller.englishButtons_Labels();

                //set up spanish labels
            } else if (c_language == 1) {
                controller.spanishButtons_Labels();
            }
            //Sets the current Language choices
            controller.setLanguageChoiceBox(c_language);

            //Signing out
        }else{
            FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/NewIntroUIView.fxml");
            //patientMenuStart.patientMenuStartController controller = loader.getController();
            NewIntroUI.NewIntroUIController controller = loader.getController();
            //sets the current language
            controller.setCurrentLanguage(c_language);
            //set permissions back
            controller.setPermissionLevel(0);
            //set up english labels
            if(c_language == 0){
                controller.englishButtons_Labels();
                //set up spanish labels
            }else if(c_language == 1){
                controller.spanishButtons_Labels();
            }
            //set label to empty
            controller.setWelcome("");
            controller.setLanguage_ChoiceBox(c_language);
            //Sets the label of the button back to administrator
            //0 In 1 out
            controller.loginOrOut(1, c_language);
        }

    }

    //Set the button correctly
    public void loginOrOut(int inOrOut, int lang){
        //The user is signing in
        if(inOrOut == 0){
            if(lang == 0){
                admin_Button.setText("Sign Out");
            }else{
                admin_Button.setText("Salir");
            }

        }else{
            if(lang == 0){
                admin_Button.setText("Administrator");
            }else{
                admin_Button.setText("Administrador");
            }

        }

    }

    //Checks if the emergency button was clicked
    public void emergencyButton_Clicked() {
        System.out.println("The user has clicked the emergency Button");
        FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/NewEmergencyView.fxml");
        emergency.emergencyController controller = loader.getController();
        //sends the current language to the next screen
        controller.setCurrentLanguage(c_language);
        //set up english labels
        if(c_language == 0){
            controller.englishButtons_Labels();
            //set up spanish labels
        }else if(c_language == 1){
            controller.spanishButtons_Labels();
        }
    }

    //Handles the action when the clear button is clicked
    public void cancelButton_Clicked(){
        //MapController.getInstance().requestMapCopy();
        selectionState = 0;

        //Remove colored dots from map

        graph.setMapAndNodes(MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),false,
                currentFloor, permissionLevel);
        c_Floor_Label.setText(Integer.toString(currentFloor));

        //wipe line from map
        graph.wipeEdgeLines();
        graph.setPathfinding(0);

        //hide the continue button
        continueNew_Button.setVisible(false);
        previous_Button.setVisible(false);

        //reset the textfields
        start_textField.setText("");
        end_TextField.setText("");
        searchError.setText("");

        //reset any colors

    }

    //switches all the labels and Buttons to english
    public void englishButtons_Labels(){
        c_language = 0;
        //change the current language to english

        //Change the Buttons
        //change the Buttons
        if(permissionLevel == 2 || permissionLevel == 1) {
            admin_Button.setText("Sign Out");
        }else {
            admin_Button.setText("Administrator");
        }
        FAQ_Button.setVisible(true);

        emergency_Button.setText("EMERGENCY");
        cancel_Button.setText("Clear");
        submit_Button.setText("Submit");
        phoneSend.setText("Send");
        about_Button.setText("About");
        MapMan_Button.setText("Map Manag.");
        adminMan_Button.setText("Admin Manag.");
        DirectoryMan_Button.setText("Directory Manag.");


        //Change the labels
        start_Label.setText("From:");
        end_Label.setText("To:");
        mainTitle_Label.setText("Welcome to Brigham and Women's Faulkner");
        floor_Label.setText("Floor");
        phoneInfo_Label.setText("Send Directions to my phone");
        threeD_Label.setText("3D Path");
        stairs_Label.setText("Allow Stairs");

        //Change the textFields
        start_textField.setPromptText("Starting position");
        end_TextField.setPromptText("Ending position");

        //Change Tabs
        textDirections_Tab.setText("Directions");
        location_Tab.setText("Locations");

        //Change choiceBox
        setFloorChoices();


    }
    //switches all teh labels to spanish
    public void spanishButtons_Labels() {
        //change the current language to spanish
        c_language = 1;
        FAQ_Button.setVisible(false);

        MapMan_Button.setText("Control de Mapa");
        adminMan_Button.setText("Control de Admins");
        DirectoryMan_Button.setText("Directorio");



        //change the Buttons
        if(permissionLevel == 2 || permissionLevel == 1) {
            admin_Button.setText("Salir");
        }else {
            admin_Button.setText("Administrador");
        }
        emergency_Button.setText("EMERGENCIA");
        cancel_Button.setText("Borrar");
        submit_Button.setText("Listo");
        phoneSend.setText("Enviar");
        about_Button.setText("Acerca");

        //change the Labels
        start_Label.setText("Inicio:");
        end_Label.setText("Destino:");
        mainTitle_Label.setText("Bienvenidos a Faulkner Brigham and Women");
        floor_Label.setText("Piso");
        phoneInfo_Label.setText("Enviar direcciones a mi celular");
        threeD_Label.setText("Camino 3D");
        stairs_Label.setText("Escaleras");


        //Change the textFields
        start_textField.setPromptText("Nombre de inicio");
        end_TextField.setPromptText("Nombre del destino");

        //Change Tabs
        textDirections_Tab.setText("Direcciones");
        location_Tab.setText("Lugares");

        //Change choiceBox
        //setFilterChoices();
        setFloorChoices();


    }

    //sets the current language given information form other screens
    public void setCurrentLanguage(int i){
        c_language = i;
    }


    public void rightClickEvent(int x, int y, Circle c, int mode) {
        System.out.println("Right click event");
    }
    public void edgeClickRemove(int x1, int y1, int x2, int y2){}

    public void sceneEvent(int x, int y, Circle c){
        //set selectionstate


        if (!(start_textField.getText().equals(""))) {
            //reset the map display
            graph.wipeEdgeLines();

            //set the correct selection state
            selectionState = 1;
        } else {

            selectionState = 0;
        }

        System.out.println("Node at (" + x + ", " + y + ") selected during state: " + selectionState);
        if (selectionState == 0) {
            //place the black marker at the starting location
            //mapController.markNode(x, y, 1, currentFloor);

            //update the txtfield
            Node myNode = mapController.getCollectionOfNodes().getNode(x, y, currentFloor);
            start_textField.setText(myNode.getName() + ", " + myNode.getRoomNum());

            selectionState++;

            //reset the colors
            resetMapNodeColors(1);

            graph.wipeEdgeLines();
            graph.setPathfinding(0);

            start = c;
            //color
            c.setStrokeWidth(strokeRatio);
            c.setStroke(startColor);
            if (c.getFill().equals(kioskColor)) {
                c.setFill(kioskColor);
            } else if (c.getFill() == Color.BLACK) {
                c.setFill(startColor);
            }


            //location
            startX = c.getLayoutX();
            startY = c.getLayoutY();
            System.out.println("Start coords updated: " + startX + "," + startY);

            //size
            c.setRadius(graph.getLabelRadius() * sizeUpRatio);

            //hide the continue and prev button if possible
            continueNew_Button.setVisible(false);
            previous_Button.setVisible(false);
        } else if (selectionState == 1){
            //place the red marker at end location
            //mapController.markNode(x, y, 2, currentFloor);

            //set the text field
            Node myNode = mapController.getCollectionOfNodes().getNode(x, y, currentFloor);
            end_TextField.setText(myNode.getName() + ", " + myNode.getRoomNum());

            //reset the colors
            resetMapNodeColors(2);

            //selectionState++;
            end = c;
            //color
            c.setStrokeWidth(strokeRatio);
            c.setStroke(endColor);
            if(c.getFill().equals(kioskColor)){
                c.setFill(kioskColor);
            } else if (c.getFill() == Color.BLACK) {
                c.setFill(endColor);
            }


            //location
            endX = c.getLayoutX();
            endY = c.getLayoutY();
            System.out.println("End coords updated: " + endX + "," + endY);

            //size
            c.setRadius(graph.getLabelRadius() * sizeUpRatio);
        } else {
            //do nothing
        }
    }

    //resets node fill colors on the map
    public void resetMapNodeColors(int mode) {
        System.out.println("resetting colors");
        if (mode == 1 || mode == 3) {
            if (start != null) {
                System.out.println("start....");
                start.setStroke(Color.BLACK);
                if ((!(start.getFill().equals(kioskColor))) && (start.getFill() == startColor)) {
                    start.setFill(Color.BLACK);
                }
                start.setStrokeWidth(1);

                //reset radius
                if (start.getFill() != Color.BLACK) {
                    start.setRadius(graph.getLabelTypeRadius());
                } else {
                    start.setRadius(graph.getLabelRadius());
                }
            }
        }
        if (mode == 2 || mode == 3) {
            if (end != null) {
                System.out.println("end....");
                end.setStroke(Color.BLACK);
                if ((!(end.getFill().equals(kioskColor))) && (end.getFill() == endColor)) {
                    end.setFill(Color.BLACK);
                }
                end.setStrokeWidth(1);

                //reset radius
                if (end.getFill() != Color.BLACK) {
                    end.setRadius(graph.getLabelTypeRadius());
                } else {
                    end.setRadius(graph.getLabelRadius());
                }
            }
        }
    }

    //Sends feedback according to the outcome of the text directions message
    public void textDirections(){
        SmsSender mySMS = new SmsSender();
        try {
            if(mySMS.sendSMSDirections(textDescription_TextFArea.getText(), phoneInsert.getText()).equals("queued")){
                phoneStatus.setText("Directions Sent.");
            }else{
                phoneStatus.setText("Failed to Send.");
            }
        } catch (URISyntaxException e){
            e.getReason();
        }
    }

    @FXML
    void directoryClicked() {

    }

    //abstracted floor refresh for multifloor pathfinding
    public void multifloorUpdate() {
        System.out.println("cf: " + currentFloor + "   size: " + globalFragList.get(fragPathPos).size());

        //otherwise, change to the appropriate screen and display edges
        graph.wipeEdgeLines();
        System.out.println("multifloor update called. Currentfloor = " + currentFloor);
        if (currentFloor == 0) {
            System.out.println("currentfloor outside!!!!");
            floor_ChoiceBox.getSelectionModel().select(7);
        } else if (currentFloor > 7) {
            floor_ChoiceBox.getSelectionModel().select(currentFloor);

        } else {
            floor_ChoiceBox.getSelectionModel().select(currentFloor - 1);
        }
        graph.setPathfinding(0);
        System.out.println("creating edge lines for fp pos: " + fragPathPos);
        graph.createEdgeLines(globalFragList.get(fragPathPos), true, false);
        graph.setPathfinding(2);
        setMapMulti(globalFragList.get(fragPathPos));


    }

    public void changeZoom(){
        graph.setZoom(zoom);
        node_Plane.setPrefWidth(origPaneWidth*zoom*widthRatio);
        node_Plane.setPrefHeight(origPaneHeight*zoom*heightRatio);
        map_viewer.setFitWidth(origPaneWidth*zoom*widthRatio);
        map_viewer.setFitHeight(origPaneHeight*zoom*heightRatio);
    }


    public void zoomInButton_Clicked() {
        zoom = graph.getZoom();
        System.out.println(zoom);
        if (zoom < 2.2) {
            zoom += 0.03;
            if (zoom > 2.2) {
                zoom = 2.2;
            }
            changeZoom();

            graph.setMapAndNodes(controllers.MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),
                    false, currentFloor, permissionLevel);
        }
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);
        if (graph.getPathfinding() == 1) {
            graph.createEdgeLines(path, true, false);
            //set the end goal color
            ArrayList<Circle> circleList;
            circleList = graph.getButtonList();
            drawCircleList(circleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
            drawCircleList(circleList, round(endX*zoom*widthRatio), round(endY*zoom*heightRatio), endColor);
            System.out.println("drawing circles at "+startX+" and "+endX);
        } else if (graph.getPathfinding() == 2) {
            graph.createEdgeLines(globalFragList.get(fragPathPos), true, false);
        }

    }

    public void zoomOutButton_Clicked() {
        zoom = graph.getZoom();
        System.out.println(zoom);
        if (zoom > 1.0) {
            zoom = zoom - 0.03;
            if (zoom < 1.0) {
                zoom = 1.0;
                scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
            }
            changeZoom();
        } else {
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
        }

        graph.setMapAndNodes(controllers.MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),
                false, currentFloor, permissionLevel);
        if (graph.getPathfinding() == 1) {
            graph.createEdgeLines(path, true, false);
            //set the end goal color
            ArrayList<Circle> circleList;
            circleList = graph.getButtonList();
            drawCircleList(circleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
            drawCircleList(circleList, round(endX*zoom*widthRatio), round(endY*zoom*heightRatio), endColor);
            System.out.println("drawing circles at "+startX+" and "+endX);
        } else if (graph.getPathfinding() == 2) {
            graph.createEdgeLines(globalFragList.get(fragPathPos), true, false);
        }
    }

    //Let the user scroll through the map
    public void mapScroll(ScrollEvent event) {

        double scrollStartX = 0,scrollStartY = 0,scrollEndX = 0,scrollEndY = 0;

//        if (!(start_textField.getText().equals(""))) {
//            Node startN = mapController.getCollectionOfNodes().getNodeWithName(start_textField.getText().split(", ")[1]);
//            scrollStartX = startN.getPosX();
//            scrollStartY = startN.getPosY();
//        }
//        if (!end_TextField.getText().split(", ")[1].equals("")) {
//            Node endN = mapController.getCollectionOfNodes().getNodeWithName(end_TextField.getText().split(", ")[1]);
//            scrollEndX = endN.getPosX();
//            scrollEndY = endN.getPosY();
//        }

        zoom = MapOverlay.getZoom();
        ArrayList<Circle> circleList;
        circleList = graph.getButtonList();
        if (currentHval != 0) {
            //System.out.println("pre zoom currenthval: " + currentHval);
            //System.out.println("pre zoom currnetVval: " + currentVval);
        }
        currentHval = scrollPane.getHvalue();
        currentVval = scrollPane.getVvalue();
        if (event.getDeltaY() > 0) {
            if (zoom < 2.2) {
                scrollPane.setFitToHeight(false);
                scrollPane.setFitToWidth(false);
                zoom += 0.03;
                if (zoom > 2.2) {
                    zoom = 2.2;
                }
                changeZoom();
                graph.setMapAndNodes(controllers.MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),
                        false, currentFloor, permissionLevel);
                if (scrollStartX != 0) {
                    drawCircleList(circleList, round(scrollStartX * zoom * widthRatio),
                            round(scrollStartY * zoom * heightRatio), startColor);
                    System.out.println("this happened i guess");
                }
                if (scrollEndX != 0) {
                    drawCircleList(circleList, round(scrollEndX * zoom * widthRatio),
                            round(scrollEndY * zoom * heightRatio), endColor);
                }
            }
        } else if (event.getDeltaY() < 0) {
            if (zoom > 1.0) {
                zoom = zoom - 0.03;
                if (zoom < 1.0) {
                    zoom = 1.0;
                    scrollPane.setFitToHeight(true);
                    scrollPane.setFitToWidth(true);
                }
                changeZoom();
                graph.setMapAndNodes(controllers.MapController.getInstance().getCollectionOfNodes().getMap(currentFloor),
                        false, currentFloor, permissionLevel);
                if (scrollStartX != 0) {
                    drawCircleList(circleList, round(scrollStartX * zoom * widthRatio),
                            round(scrollStartY * zoom * heightRatio), startColor);
                }
                if (scrollEndX != 0) {
                    drawCircleList(circleList, round(scrollEndX * zoom * widthRatio),
                            round(scrollEndY * zoom * heightRatio), endColor);
                }
            } else {
                scrollPane.setFitToHeight(true);
                scrollPane.setFitToWidth(true);
            }
        }
        if (graph.getPathfinding() == 1) {
            graph.createEdgeLines(path, true, false);
            //set the end goal color

            drawCircleList(circleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
            drawCircleList(circleList, round(endX*zoom*widthRatio), round(endY*zoom*heightRatio), endColor);

        } else if (graph.getPathfinding() == 2) {
            graph.createEdgeLines(globalFragList.get(fragPathPos), true, false);
            Node startN = mapController.getCollectionOfNodes().getNodeWithName(start_textField.getText().split(", ")[1]);
            Node endN = mapController.getCollectionOfNodes().getNodeWithName(end_TextField.getText().split(", ")[1]);

            drawCircleList(circleList, round(startN.getPosX()*zoom*widthRatio), round(startN.getPosY()*zoom*heightRatio), startColor);
            drawCircleList(circleList, round(endN.getPosX()*zoom*widthRatio), round(endN.getPosY()*zoom*heightRatio), endColor);


        }

        if (selectionState == 1) {
            drawCircleList(circleList, startX, startY, startColor);
        }
        if (selectionState == 2) {
            drawCircleList(circleList, round(startX*zoom*widthRatio), round(startY*zoom*heightRatio), startColor);
            drawCircleList(circleList, round(endX*zoom*widthRatio), round(endY*zoom*heightRatio), endColor);

        }
        scrollPane.setHvalue(currentHval);
        scrollPane.setVvalue(currentVval);
    }


    //Gets the permissions
    public int getPermissionLevel() {
        return permissionLevel;
    }

    //Sets the permissions
    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
        System.out.println("Setting permission level to: " + permissionLevel);
        if(this.permissionLevel >= 1){
            //admin_Button.setVisible(false);
            //TODO FIX THIS
            //signOut_Button.setVisible(true);
        }
    }

    //Starts the string for the current person logged in
    public void setWelcome(String text){
        LogInPerson_Label.setText(text);
    }

    public void setMapToPath(double startNX, double startNY, double endNX, double endNY) {


        System.out.println("setting map to path");
        double deltaX = startNX - endNX;
        double deltaY = startNY - endNY;

        System.out.println("startNX: "+ startNX + " endNX: "+endNX);
        double midX = (startNX + endNX)/2;
        double midY = (startNY + endNY)/2;

        if (deltaX < 0) {
            deltaX = deltaX * -1;
        }
        if (deltaY < 0) {
            deltaY = deltaY * -1;
        }

        double scrollHeight = scrollPane.getHeight();
        double scrollWidth = scrollPane.getWidth();

        System.out.println(deltaY);
        System.out.println("zoom amount: " +zoom);

        System.out.println("plane width: " + node_Plane.getWidth());
        System.out.println("midX: " + midX);
        System.out.println("previous Hvalue: " + scrollPane.getHvalue());
        System.out.println("previous Vvalue: " + scrollPane.getVvalue());

        submitHval = midX / 920;
        submitVval = midY / 489;


        System.out.println("past zoomed");
        secret_Click();
        System.out.println("after secretclick");
    }

    public void setMapMulti(ArrayList<controllers.Edge> edgeList) {
        double midX = 0;
        double midY = 0;
        double counter = 0;
        for (controllers.Edge thisEdge: edgeList) {
            if (thisEdge.getStartNode().getFloor() == thisEdge.getEndNode().getFloor()) {
                midX = midX + thisEdge.getStartNode().getPosX();
                midX = midX + thisEdge.getEndNode().getPosX();
                midY = midY + thisEdge.getStartNode().getPosY();
                midY = midY + thisEdge.getEndNode().getPosY();
                counter ++;
                counter ++;

            }
        }
        if (counter != 0) {
            midX = midX/counter;
            midY = midY/counter;
        }
        submitHval = midX / 920;
        submitVval = midY / 489;

        System.out.println("past zoomed");
        secret_Click();
        System.out.println("after secretclick");
    }

    //note: edge list must be ordered
    private Node findStartFromEdgeList(ArrayList<controllers.Edge> edgeList) {
        //if list is empty
        if (edgeList.size() == 0) {
            return null;
        }

        Node n;

        if (edgeList.size() == 1) {
            n = edgeList.get(0).getStartNode();
        } else {
            if (edgeList.get(0).getEndNode() == edgeList.get(1).getStartNode() ||
                    edgeList.get(0).getEndNode() == edgeList.get(1).getEndNode()) {
                n = edgeList.get(0).getStartNode();
            } else {
                n = edgeList.get(0).getEndNode();
            }
        }
        return n;
    }

    //note: edge list must be ordered
    private Node findEndFromEdgeList(ArrayList<controllers.Edge> edgeList) {
        //if list is empty
        if (edgeList.size() == 0) {
            return null;
        }

        Node n;

        if (edgeList.size() == 1) {
            n = edgeList.get(0).getEndNode();
        } else {
            if (edgeList.get(edgeList.size() - 1).getEndNode() == edgeList.get(edgeList.size() - 2).getStartNode() ||
                    edgeList.get(edgeList.size() - 1).getEndNode() == edgeList.get(edgeList.size() - 2).getEndNode()) {
                n = edgeList.get(edgeList.size() - 1).getStartNode();
            } else {
                n = edgeList.get(edgeList.size() - 1).getEndNode();
            }
        }
        return n;
    }


    public void setZoom(double zoom) {
        this.zoom = zoom;
    }


    public void FAQ_button_clicked() {
        FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/NewFAQ.fxml");
        FAQ.FAQcontroller controller = loader.getController();
        controller.setAdmin(LogInPerson_Label.getText());

        controller.setPermissionLevel(getPermissionLevel());
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

    //Sets the buttons to the admin
    public void AdminButtons(int lang){
        if(lang == 0){
            MapMan_Button.setText("Map Manag.");
            adminMan_Button.setText("Admin Manag.");
            DirectoryMan_Button.setText("Directory Manag.");
        }else{
            MapMan_Button.setText("Control de Mapa");
            adminMan_Button.setText("Control de Admins");
            DirectoryMan_Button.setText("Directorio");
        }

        MapMan_Button.setDisable(false);
        adminMan_Button.setDisable(false);
        DirectoryMan_Button.setDisable(false);

        MapMan_Button.setVisible(true);
        adminMan_Button.setVisible(true);
        DirectoryMan_Button.setVisible(true);
    }

    //sends the user to the map management
    public void mapMan_Clicked(){
        FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/NewMainMapManagementView.fxml");
        NewMainMapManagement.NewMainMapManagementController controller = loader.getController();
        //Set the correct username for the next scene

        //set up english labels
        if(c_language == 0){
            controller.englishButtons_Labels();

            //set up spanish labels
        }else if(c_language == 1){
            controller.spanishButtons_Labels();
        }
        controller.setUserString(LogInPerson_Label.getText());
        controller.setPermissionLevel(2);


    }

    //sends the user to the admin management
    public void adminMan_Clicked(){
//Change to patient menu
        FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/NewAdminManagementView.fxml");
        adminSignUp.adminSignUpController controller = loader.getController();
        //sends the current language to the next screen
        controller.setCurrentLanguage(c_language);
        //Gets the current admin
        controller.setUsername(LogInPerson_Label.getText());

        //set up english labels

        if(c_language == 0){
            controller.englishButtons_Labels();
            //set up spanish labels
        }else if(c_language == 1){
            controller.spanishButtons_Labels();
        }

        controller.setUpTreeView();
        controller.setModeChoices();

    }

    //sends the user to the directory management
    public void directoryMan_Clicked(){

        FXMLLoader loader= switch_screen(backgroundAnchorPane, "/views/NewDirectoryManagementView.fxml");
        mapManagementNodeInformation.mmNodeInformationController controller = loader.getController();

        //sets the current language
        controller.setC_language(c_language);

        controller.setModeChoices();
        controller.setRoomChoices();
        controller.setUpTreeView();
        controller.setUser(LogInPerson_Label.getText());

        //set up english labels
        if(c_language == 0){
            controller.englishButtons_Labels();

            //set up spanish labels
        }else if(c_language == 1){
            controller.spanishButtons_Labels();
        }
        //Set permissions of admin
        controller.setPermissionLevel(2);


    }

    public void secret_Click() {

        System.out.println("in secretclick");
        System.out.println("current-Hval = " + currentHval);
        System.out.println("current-Vval = " + currentVval);
        System.out.println("pane-Hval = " + scrollPane.getHvalue());
        System.out.println("pane-Vval = " + scrollPane.getVvalue());

        if (submitHval != 0) {
            scrollPane.setHvalue(submitHval);
            scrollPane.setVvalue(submitVval);
        }
        System.out.println(submitHval + "   " +submitVval);
        System.out.println("New Hvalue: " + scrollPane.getHvalue());
        System.out.println("New Vvalue: " + scrollPane.getVvalue());
    }
}




