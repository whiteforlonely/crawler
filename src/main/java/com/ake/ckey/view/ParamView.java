package com.ake.ckey.view;

import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextAlignment;

/**
 * 参数行
 */
public class ParamView<C extends Control> {

    // 输入框类型
    private C component;

    private HBox finalView;

    private ComponentValueUpdateListener listener;

    public ParamView(String labelName, C component) {
        this.component = component;

        Label label = new Label(labelName);
        label.setMinWidth(80);
        label.setTextAlignment(TextAlignment.LEFT);
        finalView = new HBox(label, component);
        this.finalView.setPadding(new Insets(5));

        if (this.component instanceof TextField) {
            TextField textField = (TextField) this.component;
            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println("[ParamView] come to setOnInputMethodTextChanged！inputMethodEvent=" + newValue);
                if (null != listener) {
                    listener.handle(newValue);
                }
            });
        }
    }

    public void setListener(ComponentValueUpdateListener listener) {
        this.listener = listener;
    }

    public HBox getRoot(){
        return this.finalView;
    }

    public C getInput(){
        return this.component;
    }

    public interface ComponentValueUpdateListener{

        void handle(String value);
    }
}
