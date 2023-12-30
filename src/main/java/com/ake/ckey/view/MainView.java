package com.ake.ckey.view;

import com.ake.ckey.model.TaskModel;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jsoup.internal.StringUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 16:06
 */
public class MainView {

    private ToolBar toolBar;
    private List<ReqView> reqViews;
    private Button createTaskBtn;
    private TasksDialog tasksDialog;

    private VBox container;

    public MainView(){
        this.reqViews = new ArrayList<>();
        initToolbar();
    }

    private void initToolbar(){
        this.createTaskBtn = new Button("创建新任务");
        Button taskListBtn =new Button("查看任务");
        tasksDialog = new TasksDialog();
        taskListBtn.setOnMouseClicked(e -> {
            tasksDialog.show();
        });
        tasksDialog.setTaskClickCallback(item -> initReqView(item));
        this.toolBar = new ToolBar(this.createTaskBtn, taskListBtn);
        this.createTaskBtn.setOnAction(e -> {
            ReqView reqView = new ReqView();
            this.reqViews.add(reqView);
            Iterator<ReqView> iterator = reqViews.iterator();
            while (iterator.hasNext()) {
                ReqView rv = iterator.next();
                if (StringUtil.isBlank(rv.getTitle())) {
                    iterator.remove();
                }
            }
            if (this.container.getChildren().size() == 1) {
                this.container.getChildren().add(reqView.getRoot());
            } else {
                this.container.getChildren().set(1, reqView.getRoot());
            }
        });

        this.container =new VBox();
        this.container.setFillWidth(true);
        this.container.setMinHeight(700);
        this.container.setMinWidth(1200);
        this.container.getChildren().add(toolBar);
    }

    private void initReqView(TaskModel taskModel){
        ReqView currReqView = null;
        for (ReqView reqView : reqViews) {
            if (reqView.getTitle().equals(taskModel.getTaskUrl())) {
                currReqView = reqView;
                break;
            }
        }
        if (currReqView == null) {
            currReqView = new ReqView();
            currReqView.renderView(taskModel);
            reqViews.add(currReqView);
        }
        if (this.container.getChildren().size() == 1) {
            this.container.getChildren().add(currReqView.getRoot());
        } else {
            this.container.getChildren().set(1, currReqView.getRoot());
        }
    }

    public VBox getRoot(){
        return this.container;
    }

    public List<ReqView> getReqViews() {
        return reqViews;
    }
}
