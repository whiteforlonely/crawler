package com.ake.ckey.view;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/27 12:02
 */
public class LoadingDialog {

    private Alert alert;

    private ImageView imageView;

    public LoadingDialog(){
        alert = new Alert(Alert.AlertType.NONE);
        ImageView loadingView = new ImageView(
                new Image("https://blog-static.cnblogs.com/files/miaoqx/loading.gif"));// 可替换
        Label messageLb = new Label("请耐心等待...");
        messageLb.setFont(Font.font(20));

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(loadingView, messageLb);

        alert.getDialogPane().setContent(stackPane);
    }

    public void show(){
        this.alert.show();
    }

    public void hide(){
        this.alert.hide();
    }

    public Alert getRoot(){
        return this.alert;
    }
}
