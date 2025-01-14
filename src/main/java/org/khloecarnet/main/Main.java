package org.khloecarnet.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader loader =new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        Parent root =loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("天书计算器");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
