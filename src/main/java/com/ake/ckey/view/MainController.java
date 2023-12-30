package com.ake.ckey.view;

import javafx.scene.Scene;

/**
 * 专门生产组件的地方
 */
public class MainController {

    private MainView window;

    public Scene initWindow(){
        window = new MainView();
        return new Scene(window.getRoot());
    }

    public void refreshTasks(){
        for (ReqView reqView : this.window.getReqViews()) {
            reqView.refreshTask();
        }
    }

}
