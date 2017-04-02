package adminLoginMain;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * Created by AugustoR on 4/1/17.
 */
public class adminLoginMainController extends controllers.AbsController{
    @FXML
    private TextField username_TextField;

    @FXML
    private PasswordField password_PasswordField;

    @FXML
    private AnchorPane backgroundAnchorPane;

    @FXML
    private Button logIn_Button;

    @FXML
    private Button mainMenu_Button;

    public void logInButton_Clicked(){
        AdminLoginManager loginManage = new AdminLoginManager();
        String username = username_TextField.getText();
        String password = password_PasswordField.getText();

        System.out.println("The user has clicked the log in Button");
        System.out.println(username);
        System.out.println(password);

        if(loginManage.verifyCredentials(username, password) == 1){
            System.out.println("correct Password");

            FXMLLoader loader = switch_screen(backgroundAnchorPane, "/views/adminMenuStartView.fxml");
            //adminMenuStartController controller = loader.getController();
            //setUsername(username_TextField.getText());
        }


    }

    public void mainMenuButton_Clicked(){

        System.out.println("The user has clicked the main menu Button");
        switch_screen(backgroundAnchorPane, "/views/patientMenuStartView.fxml");

    }




}
