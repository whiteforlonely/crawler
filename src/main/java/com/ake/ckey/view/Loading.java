package com.ake.ckey.view;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 加载页
 *
 * @author Miaoqx
 */
public class Loading {

    protected Stage stage;
    protected StackPane root;
    protected Label messageLb;

    public Loading(Stage owner) {
        ImageView loadingView = new ImageView(
                new Image("https://hbimg.b0.upaiyun.com/f47dd9d917c9e5bd1680532a4ebc12ae17153d9b1b380d-J5NBTJ_fw658"));// 可替换
        loadingView.setFitWidth(100);
        loadingView.setFitHeight(100);

        messageLb = new Label("请耐心等待...");
        messageLb.setFont(Font.font(20));

        root = new StackPane();
        root.setMouseTransparent(true);
        root.setPrefSize(owner.getWidth(), owner.getHeight());
        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null)));
        root.getChildren().addAll(loadingView, messageLb);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initOwner(owner);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().addAll(owner.getIcons());
        stage.setX(owner.getX());
        stage.setY(owner.getY());
        stage.setHeight(owner.getHeight());
        stage.setWidth(owner.getWidth());
//        stage.setAlwaysOnTop(false);
    }

    // 更改信息
    public void showMessage(String message) {
        Platform.runLater(() -> messageLb.setText(message));
    }

    // 显示
    public void show() {
        Platform.runLater(() -> stage.show());
    }

    // 关闭
    public void closeStage() {
        Platform.runLater(() -> stage.close());
    }
}