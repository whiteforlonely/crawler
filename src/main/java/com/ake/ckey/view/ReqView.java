package com.ake.ckey.view;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.ake.ckey.controller.AlertController;
import com.ake.ckey.enums.LanguageEnum;
import com.ake.ckey.enums.TaskStatusEnum;
import com.ake.ckey.model.*;
import com.ake.ckey.service.BizCacheService;
import com.ake.ckey.service.BizCacheServiceImpl;
import com.ake.ckey.service.BizCrawlerServiceImpl;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.jsoup.internal.StringUtil;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/21 15:45
 */
public class ReqView {

    private CrawlerReq crawlerReq;
    private String title;

    private TextField userNameField;
    private ParamView<TextField> userNameParamView;
    private TextField urlField;
    private ParamView<TextField> urlParamView;
    private ComboBox<LanguageEnum> languageComboBox;
    private ParamView<ComboBox<LanguageEnum>> languageParamView;
    private Button addStepBtn;
    private Button cancelBtn;
    private Button confirmBtn;
    private ScrollPane stepContainer;
    private HBox stepBox;
    private VBox container = new VBox();

    private Label dataCountLabel;
    private Label runningInfoLabel;

    private LinkedList<DataStepView> stepViews;

    private TableView<MerchantInfoModel> dataTable;

    private BizCrawlerServiceImpl crawlerService = new BizCrawlerServiceImpl();
    private BizCacheService cacheService = new BizCacheServiceImpl();

    private Map<String, Future<?>> taskMap = new HashMap<>();
    private TaskModel taskModel;
    private String countText = "已拉取数据：%s条";
//    Loading loading;

    public ReqView(){
        this.crawlerReq = new CrawlerReq();
        this.stepViews = new LinkedList<>();
        this.dataCountLabel = new Label(String.format(countText, "0"));
        this.dataCountLabel.setAlignment(Pos.CENTER_RIGHT);
        this.runningInfoLabel = new Label("");
        this.runningInfoLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        this.runningInfoLabel.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(this.runningInfoLabel, Priority.ALWAYS);


        this.userNameField = new TextField();
        this.userNameField.setPromptText("输入名字，作为爬数据的跟踪统计依据");
        this.urlField = new TextField();
        this.urlField.setPromptText("请输入网站首页网址");
        this.languageComboBox = new ComboBox<>();
        this.languageComboBox.setConverter(new StringConverter<LanguageEnum>() {
            @Override
            public String toString(LanguageEnum languageEnum) {
                if (languageEnum == null) {
                    languageEnum = LanguageEnum.EN_US;
                }
                return languageEnum.getChineseDesc();
            }

            @Override
            public LanguageEnum fromString(String s) {
                return LanguageEnum.descMap.get(s);
            }
        });
        this.languageComboBox.getItems().addAll(LanguageEnum.values());
        this.languageComboBox.setValue(LanguageEnum.EN_US);

        this.userNameParamView = new ParamView<>("用户名", this.userNameField);
        this.urlParamView = new ParamView<>("网址", this.urlField);
        this.languageParamView = new ParamView<>("网站语种", this.languageComboBox);
        this.container = new VBox();
        this.container.getChildren().add(this.userNameParamView.getRoot());
        this.container.getChildren().add(this.urlParamView.getRoot());
        this.container.getChildren().add(this.languageParamView.getRoot());


        this.addStepBtn = new Button("添加下一步");
        ParamView<Button> addStepParamView = new ParamView<>("数据查找轨迹", addStepBtn);

//        hBox.setPadding(new Insets(10));
        this.container.getChildren().add(addStepParamView.getRoot());

        this.stepContainer = new ScrollPane();
        this.stepContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.stepContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.stepContainer.setMinHeight(260);
        this.stepContainer.setMinWidth(500);
        this.stepBox =new HBox();
        this.stepBox.setMaxWidth(Double.NEGATIVE_INFINITY);
        this.stepBox.setSpacing(10);
        this.stepContainer.setContent(stepBox);
        this.container.getChildren().add(this.stepContainer);
        this.container.getChildren().add(new Separator());

        HBox bottomView = new HBox();
        bottomView.setAlignment(Pos.BOTTOM_RIGHT);
        this.cancelBtn =new Button("结束任务");
        this.confirmBtn = new Button("开始");
        bottomView.getChildren().add(this.dataCountLabel);
        bottomView.getChildren().add(cancelBtn);
        bottomView.getChildren().add(confirmBtn);
        bottomView.setPadding(new Insets(10));
        bottomView.setSpacing(10);

        this.container.getChildren().add(bottomView);

        this.dataTable = new TableView<>();
        initTableView();
        this.container.getChildren().add(this.dataTable);
        VBox.setVgrow(this.dataTable, Priority.ALWAYS);

//        this.loading = new Loading(stage);

        initEvent();
    }

    private void initEvent(){
        this.userNameParamView.setListener(s -> this.crawlerReq.setUserName(s));
        this.urlParamView.setListener(s -> {
            this.crawlerReq.setUrl(s);
            this.title = s;
        });
        this.languageComboBox.getSelectionModel().select(LanguageEnum.EN_US);
//        this.languageComboBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldVal, newVal) -> crawlerReq.setLanguageCode(newVal.getLanguageCode()));
        this.addStepBtn.setOnAction(event -> {
            DataStepView stepView = new DataStepView();
            this.stepViews.add(stepView);
            stepView.setCloseEvent(c -> {
                this.stepViews.pop();
                this.stepBox.getChildren().remove(c);
            });
            this.stepBox.getChildren().add(stepView.getRoot());
        });
        this.confirmBtn.setOnMouseClicked(event -> {
            // 开始爬取数据
//            GlobalThreadPool.execute(() -> {
//                loading.show();
//                loading.showMessage("网页数据分析中...");
//            });
            Platform.runLater(() -> {
                this.confirmBtn.setText("开始分析网页数据...");
                this.confirmBtn.setDisable(true);
            });

            GlobalThreadPool.execute(() -> {
                startCrawler();
            });
        });
        this.crawlerService.setTaskStatusCallback(taskStatusEnum -> {
            this.taskModel.setStatusCode(taskStatusEnum.getCode());
            this.taskModel.setStatus(taskStatusEnum.getDesc());
            if (taskStatusEnum == TaskStatusEnum.DONE || taskStatusEnum == TaskStatusEnum.EXCEPTION) {
//                GlobalThreadPool.execute(() -> {
//                    if (null != this.loading) {
//                        this.loading.closeStage();
//                    }
//                });
                Platform.runLater(() ->{
                    this.confirmBtn.setText("启动任务");
                    if (taskStatusEnum == TaskStatusEnum.DONE) {
                        this.confirmBtn.setDisable(true);
                    } else {
                        this.confirmBtn.setDisable(false);
                    }
                });
                this.taskModel.setFinishedTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
                refreshTask();
            }
            Platform.runLater(() ->{
                updateStartBtn(0, taskStatusEnum);
            });
        });
        this.crawlerService.setCounterRemindCallback(count -> {
            this.taskModel.setDataCount(count);
            Platform.runLater(() -> {
                // 针对于子线程执行UI变化的问题
                this.dataCountLabel.setText(String.format(countText, String.valueOf(count)));
            });

        });
        this.crawlerService.setReadyCallback(() -> Platform.runLater(() -> this.confirmBtn.setText("开始爬取数据...")));
        this.crawlerService.setLogCallback(text -> Platform.runLater(()-> this.runningInfoLabel.setText(text)));
        this.cancelBtn.setOnMouseClicked(event -> {
            if (null != this.taskModel) {
                updateStartBtn(0, TaskStatusEnum.EXCEPTION);
                this.taskModel.setStatusCode(TaskStatusEnum.EXCEPTION.getCode());
                this.taskModel.setStatus("已停止");
                refreshTask();
            }
        });
    }

    public void refreshTask(){
        if (null != this.taskModel) {
            Future<?> future = AppConfig.globalTaskFutureMap.get(this.taskModel.getTaskName());
            if (null != future && !future.isDone()) {
                this.crawlerService.setStopTag(true);
            }
            if (this.taskModel.getStatusCode() == TaskStatusEnum.RUNNING.getCode()) {
                this.taskModel.setStatusCode(TaskStatusEnum.EXCEPTION.getCode());
                this.taskModel.setStatus(TaskStatusEnum.EXCEPTION.getDesc());
            }
            Object taskListObj = cacheService.getProperty(BizCacheService.TASK_LIST);
            if (taskListObj == null) {
                List<TaskModel> taskList = new ArrayList<>();
                taskList.add(taskModel);
                cacheService.cacheProperty(BizCacheService.TASK_LIST, taskList);
            } else {
                List<TaskModel> taskList = JSONUtil.toList((JSONArray) taskListObj, TaskModel.class);
                boolean changed = false;
                for (TaskModel model : taskList) {
                    if (model.getTaskName().equals(taskModel.getTaskName())) {
                        BeanUtil.copyProperties(taskModel, model);
                        changed = true;
                    }
                }
                if (!changed) {
                    // 不是更改任务数据，而是增加数据
                    taskList.add(taskModel);
                }
                cacheService.cacheProperty(BizCacheService.TASK_LIST, taskList);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void renderView(TaskModel taskModel){
        this.crawlerReq = taskModel.getReqData();
        this.taskModel = taskModel;
        updateStartBtn(1, TaskStatusEnum.typeOf(taskModel.getStatusCode()));
        this.title = this.taskModel.getTaskUrl();
        this.urlParamView.getInput().setText(this.crawlerReq.getUrl());
        this.userNameParamView.getInput().setText(this.crawlerReq.getUserName());
        if (!StringUtil.isBlank(this.crawlerReq.getLanguageCode())) {
            this.languageComboBox.getSelectionModel().select(LanguageEnum.codeMap.get(this.crawlerReq.getLanguageCode()));
        }
        for (CrawlerStepReq step : this.crawlerReq.getSteps()) {
            DataStepView stepView = new DataStepView();
            stepView.renderView(step, this.crawlerReq.getMerchantInfoReq());
            stepView.setCloseEvent(event ->{
                this.stepViews.pop();
                this.stepBox.getChildren().remove(stepView);
            });
            this.stepViews.add(stepView);
            this.stepBox.getChildren().add(stepView.getRoot());
        }
        // 初始化拉取数量
        this.dataCountLabel.setText(String.format(countText, String.valueOf(taskModel.getDataCount())));
    }

    private void updateStartBtn(int source, TaskStatusEnum taskStatusEnum) {
        if (taskStatusEnum == TaskStatusEnum.DONE) {
            this.confirmBtn.setText("任务已完成");
            this.confirmBtn.setDisable(true);
        } else if (taskStatusEnum == TaskStatusEnum.EXCEPTION) {
            this.confirmBtn.setText("重新启动任务");
            this.confirmBtn.setDisable(false);
        } else if (taskStatusEnum == TaskStatusEnum.READY) {
            this.confirmBtn.setText("启动任务");
            this.confirmBtn.setDisable(false);
        } else if (taskStatusEnum == TaskStatusEnum.RUNNING) {
            if (source == 1) {
                this.confirmBtn.setText("重新启动任务");
                this.confirmBtn.setDisable(false);
            } else {
                this.confirmBtn.setText("进行中");
                this.confirmBtn.setDisable(true);
            }
        }
    }

    private void startCrawler(){
        this.crawlerService.setDataTable(this.dataTable);
        this.crawlerService.setStopTag(false);
        this.crawlerReq.setLanguageCode(this.languageComboBox.getSelectionModel().getSelectedItem().getLanguageCode());
        if (StringUtil.isBlank(this.crawlerReq.getUrl())) {
            Platform.runLater(()->{
                AlertController.exception("参数错误", "网址不能为空");
                this.confirmBtn.setDisable(false);
                this.confirmBtn.setText("启动任务");
            });

            return;
        }
        if (StringUtil.isBlank(this.crawlerReq.getUserName())) {
            Platform.runLater(()->{
                AlertController.exception("参数错误", "用户名不能为空");
                this.confirmBtn.setDisable(false);
                this.confirmBtn.setText("启动任务");
            });

            return;
        }
        this.crawlerReq.setSteps(new ArrayList<>());
        for (DataStepView stepView : this.stepViews) {
            CrawlerStepReq stepData = stepView.getStepData();
            this.crawlerReq.getSteps().add(stepData);
            if (stepData.getFinalInfoLink() != null && stepData.getFinalInfoLink()) {
                this.crawlerReq.setMerchantInfoReq(stepView.getMerchantInfo());
            }
        }

        // 保存在本地缓存中
        String taskName = DigestUtil.md5Hex(this.crawlerReq.getUrl());
        if (taskModel == null) {
            taskModel = new TaskModel();
            taskModel.setTaskName(taskName);
            taskModel.setStartTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            taskModel.setTaskUrl(this.crawlerReq.getUrl());
        }
        taskModel.setReqData(this.crawlerReq);
        refreshTask();

        // 开始执行任务
        Future<?> taskFuture = crawlerService.crawlData(this.crawlerReq);
        if (taskFuture != null) {
            cacheService.updateTaskStatus(taskName, TaskStatusEnum.RUNNING);
            Platform.runLater(() -> updateStartBtn(0, TaskStatusEnum.RUNNING));
            // 加入到任务列表中
            AppConfig.globalTaskFutureMap.put(taskName, taskFuture);
        }
    }

    public VBox getRoot(){
        return this.container;
    }

    private void initTableView(){

        TableColumn<MerchantInfoModel, String> merchantNameColumn = new TableColumn<>("企业名称");
        merchantNameColumn.setCellValueFactory(new PropertyValueFactory<>("merchantName"));
        TableColumn<MerchantInfoModel, String> merchantWebsiteColumn = new TableColumn<>("官网");
        merchantWebsiteColumn.setCellValueFactory(new PropertyValueFactory<>("merchantWebsite"));
        TableColumn<MerchantInfoModel, String> merchantCategoryColumn = new TableColumn<>("行业类别");
        merchantCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("merchantIndustryLargeCategory"));
        TableColumn<MerchantInfoModel, String> merchantCountryColumn = new TableColumn<>("国家");
        merchantCountryColumn.setCellValueFactory(new PropertyValueFactory<>("merchantCountry"));
        TableColumn<MerchantInfoModel, String> merchantProvinceColumn = new TableColumn<>("省份");
        merchantProvinceColumn.setCellValueFactory(new PropertyValueFactory<>("merchantProvince"));
        TableColumn<MerchantInfoModel, String> merchantCityColumn = new TableColumn<>("城市");
        merchantCityColumn.setCellValueFactory(new PropertyValueFactory<>("merchantCity"));
        TableColumn<MerchantInfoModel, String> merchantAddrColumn = new TableColumn<>("地址");
        merchantAddrColumn.setCellValueFactory(new PropertyValueFactory<>("merchantAddress"));
        TableColumn<MerchantInfoModel, String> merchantPhoneColumn = new TableColumn<>("电话");
        merchantPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("merchantMobilePhone"));
        TableColumn<MerchantInfoModel, String> merchantEmailColumn = new TableColumn<>("邮箱");
        merchantEmailColumn.setCellValueFactory(new PropertyValueFactory<>("merchantEmail"));
        TableColumn<MerchantInfoModel, String> merchantEstablishColumn = new TableColumn<>("成立时间");
        merchantEstablishColumn.setCellValueFactory(new PropertyValueFactory<>("establishDate"));
        TableColumn<MerchantInfoModel, String> merchantLicenseCodeColumn = new TableColumn<>("统一社会信用号");
        merchantLicenseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("merchantBusinessLicenseCode"));
        TableColumn<MerchantInfoModel, String> merchantDescColumn = new TableColumn<>("企业简介");
        merchantDescColumn.setCellValueFactory(new PropertyValueFactory<>("businessScope"));
        TableColumn<MerchantInfoModel, String> merchantBizCodeColumn = new TableColumn<>("行业代码");
        merchantBizCodeColumn.setCellValueFactory(new PropertyValueFactory<>("bizCode"));
        TableColumn<MerchantInfoModel, String> merchantLogoColumn = new TableColumn<>("商标");
        merchantLogoColumn.setCellValueFactory(new PropertyValueFactory<>("merchantLogo"));
        TableColumn<MerchantInfoModel, String> merchantImageColumn = new TableColumn<>("宣传图");
        merchantImageColumn.setCellValueFactory(new PropertyValueFactory<>("merchantImage"));
        TableColumn<MerchantInfoModel, String> merchantPostColumn = new TableColumn<>("邮编");
        merchantPostColumn.setCellValueFactory(new PropertyValueFactory<>("postInfo"));
        TableColumn<MerchantInfoModel, String> merchantCapitalColumn = new TableColumn<>("注册资产");
        merchantCapitalColumn.setCellValueFactory(new PropertyValueFactory<>("registeredCapital"));

        this.dataTable.getColumns().addAll(merchantNameColumn,merchantWebsiteColumn,merchantCategoryColumn,merchantCountryColumn,merchantProvinceColumn,merchantCityColumn,
                merchantAddrColumn,merchantPhoneColumn,merchantEmailColumn,merchantEstablishColumn,merchantLicenseCodeColumn,merchantDescColumn,merchantBizCodeColumn,
                merchantLogoColumn,merchantImageColumn,merchantPostColumn,merchantCapitalColumn
        );
    }
}
