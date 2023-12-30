package com.ake.ckey.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AlertController {

    public static void exception(String message, String content){
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("异常提示");
            alert.setContentText(content);
            alert.setHeaderText(message);
            alert.showAndWait();
        });
    }
}
