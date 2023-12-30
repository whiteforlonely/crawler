package com.ake.ckey;

import com.ake.ckey.view.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        MainController mainController = new MainController();
        Scene scene = mainController.initWindow();
        stage.setScene(scene);
        stage.setTitle("CKEY爬虫工具");
        stage.setOnCloseRequest(event -> mainController.refreshTasks());
//        stage.setIconified(true);
        InputStream inputStream = getClass().getResourceAsStream("/assets/logo.jpg");
        stage.getIcons().add(new Image(inputStream));
        stage.show();
        stage.setAlwaysOnTop(false);

//        mainController.refreshView();
    }

    public static void main(String[] args) {
        launch();
    }
}
