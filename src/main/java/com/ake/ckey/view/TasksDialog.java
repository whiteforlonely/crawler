package com.ake.ckey.view;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.ake.ckey.callback.TaskClickCallback;
import com.ake.ckey.model.TaskModel;
import com.ake.ckey.service.BizCacheService;
import com.ake.ckey.service.BizCacheServiceImpl;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 9:03
 */
public class TasksDialog {

    private Alert alert;
    private BizCacheService cacheService;
    private TableView<TaskModel> tableView;
    private TaskClickCallback taskClickCallback;

    public void setTaskClickCallback(TaskClickCallback taskClickCallback) {
        this.taskClickCallback = taskClickCallback;
    }

    public TasksDialog(){
        tableView = new TableView<>();
        cacheService = new BizCacheServiceImpl();

        this.alert = new Alert(Alert.AlertType.INFORMATION);
        this.alert.setTitle("任务列表");
        this.alert.setHeaderText("之前爬过的所有的历史任务记录");

        this.alert.getDialogPane().setContent(this.tableView);
        initTableView();
    }

    private void initTableView(){
        TableColumn<TaskModel, String> taskIdColumn = new TableColumn<>("任务ID");
        taskIdColumn.setCellValueFactory(new PropertyValueFactory<>("taskName"));
        TableColumn<TaskModel, String> taskUrlColumn = new TableColumn<>("网址");
        taskUrlColumn.setCellValueFactory(new PropertyValueFactory<>("taskUrl"));
        TableColumn<TaskModel, String> startTimeColumn = new TableColumn<>("开始时间");
        startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        TableColumn<TaskModel, String> endTimeColumn = new TableColumn<>("结束时间");
        endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("finishedTime"));
        TableColumn<TaskModel, String> reqColumn = new TableColumn<>("请求参数");
        reqColumn.setCellValueFactory(new PropertyValueFactory<>("reqData"));
        TableColumn<TaskModel, String> statusColumn = new TableColumn<>("状态");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<TaskModel, String> dataCountColumn = new TableColumn<>("已获取数据量");
        dataCountColumn.setCellValueFactory(new PropertyValueFactory<>("dataCount"));
        this.tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        this.tableView.setMinWidth(800);
        this.tableView.getColumns().addAll(taskIdColumn, taskUrlColumn, startTimeColumn, endTimeColumn, statusColumn, dataCountColumn, reqColumn);

        this.tableView.setRowFactory(tv -> {
            TableRow<TaskModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TaskModel item = row.getItem();
                    if (taskClickCallback != null) {
                        taskClickCallback.handTask(item);
                        this.alert.hide();
                    }
                }
            });
            return row;
        });
    }

    public void show(){
        Object taskListObj = cacheService.getProperty(BizCacheService.TASK_LIST);
        if (null != taskListObj){
            List<TaskModel> taskList = JSONUtil.toList((JSONArray) taskListObj, TaskModel.class);
            this.tableView.getItems().clear();
            this.tableView.getItems().addAll(taskList);
            this.tableView.refresh();
        }
        this.alert.showAndWait();
    }
}
