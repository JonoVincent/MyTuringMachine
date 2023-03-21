import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;



public class App extends Application {

    @Override
    public void start(Stage primaryStage){
        
        Parent root;
        try {

            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

            root = FXMLLoader.load(getClass().getResource("mtmInterface.fxml"));
            Scene scene = new Scene(root);

            primaryStage.setTitle("My Turing Machine");
            primaryStage.setScene(scene);
            primaryStage.setX(primaryScreenBounds.getMinX());
            primaryStage.setY(primaryScreenBounds.getMinY());
            primaryStage.setHeight(primaryScreenBounds.getHeight());
            primaryStage.setWidth(primaryScreenBounds.getWidth());

            Image MTM_Icon = new Image("My_Turing_Machine_Icon.png");
            primaryStage.getIcons().add(MTM_Icon);

            primaryStage.show();

            scene.getStylesheets().add(getClass().getResource("MainScene_Style.css").toExternalForm());

        } catch (IOException e) {
        }
    }



    public static void main(String[] args) throws Exception {
        launch(args);
    }
     
}
