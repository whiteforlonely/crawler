package com.ake.ckey.view;

import com.ake.ckey.enums.PageTypeEnum;
import com.ake.ckey.model.CrawlerStepReq;
import com.ake.ckey.model.MerchantInfoModel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/21 11:03
 */
public class DataStepView {

    private CrawlerStepReq stepData;

    private TextField labelField;
    private TextField urlField;
    private ToggleButton finalInfoLinkToggleButton;
    private ToggleButton paginateToggleButton;
    private ChoiceBox<PageTypeEnum> pageTypeChoiceBox;
    private TextField pageUrlsField;
    private TextField nextPageLabelField;

    private ParamView<TextField> labelParamView;
    private ParamView<TextField> urlParamView;
    private ParamView<ToggleButton> finalLinkInfoParamView;
    private ParamView<ToggleButton> paginateParamView;
    private ParamView<ChoiceBox<PageTypeEnum>> pageTypeParamView;
    private ParamView<TextField> pageUrlsParamView;
    private ParamView<TextField> nextPageLabelParamView;

    private Button editMerchantBtn;
    private MerchantInfoDialog merchantInfoDialog;

    private VBox container;

    private Button closeBtn;
    private HBox topBar;

    private CloseEvent closeEvent;

    public DataStepView(){
        this.stepData = new CrawlerStepReq();
        this.labelField = new TextField();
        this.urlField = new TextField();
        this.finalInfoLinkToggleButton = new ToggleButton("NO");
        this.finalInfoLinkToggleButton.setSelected(false);
        this.paginateToggleButton = new ToggleButton("NO");
        this.paginateToggleButton.setSelected(false);
        this.pageTypeChoiceBox = new ChoiceBox<>();
        this.pageTypeChoiceBox.getItems().addAll(PageTypeEnum.values());
        this.pageTypeChoiceBox.setConverter((new StringConverter<>() {
            @Override
            public String toString(PageTypeEnum pageTypeEnum) {
                if (pageTypeEnum != null) {
                    return pageTypeEnum.getDesc();
                } else {
                    return PageTypeEnum.NUMBER.getDesc();
                }
            }

            @Override
            public PageTypeEnum fromString(String s) {
                return PageTypeEnum.typeOf(s);
            }
        }));
        this.pageTypeChoiceBox.setValue(PageTypeEnum.NUMBER);
        this.pageUrlsField = new TextField();
        this.nextPageLabelField = new TextField();

        this.labelParamView = new ParamView<>("点击文本", this.labelField);
        this.urlParamView = new ParamView<>("当前网址", this.urlField);
        this.finalLinkInfoParamView = new ParamView<>("是否信息页", this.finalInfoLinkToggleButton);
        this.paginateParamView = new ParamView<>("是否分页", this.paginateToggleButton);
        this.pageTypeParamView = new ParamView<>("分页类型", this.pageTypeChoiceBox);
        this.pageUrlsParamView = new ParamView<>("下页地址", this.pageUrlsField);
        this.nextPageLabelParamView = new ParamView<>("下一页文本", this.nextPageLabelField);


        this.container = new VBox();
        this.container.setMinHeight(200);
        this.container.setBorder(new Border(new BorderStroke(Paint.valueOf("#aaa"), BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))));
        this.closeBtn = new Button("X");
        this.closeBtn.setBorder(new Border(new BorderStroke(Paint.valueOf("#aaa"), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(1))));
        this.closeBtn.setBackground(new Background(new BackgroundFill(Paint.valueOf("#fff"), new CornerRadii(20), new Insets(5))));
        this.closeBtn.setPadding(new Insets(7, 11, 7, 11));
        this.topBar = new HBox();
        this.topBar.setAlignment(Pos.BOTTOM_RIGHT);
        this.topBar.setPadding(new Insets(5, 5, 5,5));
//        this.topBar.setBorder(new Border(new BorderStroke(Paint.valueOf("#f00"), BorderStrokeStyle.SOLID, new CornerRadii(20), new BorderWidths(1))));
        this.topBar.setMinWidth(300);
        this.topBar.getChildren().add(this.closeBtn);

        this.editMerchantBtn =new Button("编辑企业模板信息");
        this.editMerchantBtn.setPadding(new Insets(5, 10, 5, 10));
        this.merchantInfoDialog = new MerchantInfoDialog(new MerchantInfoModel());

        initEvent();
        syncModelView();
    }

    private void initEvent(){
        this.finalInfoLinkToggleButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            this.stepData.setFinalInfoLink(t1);
            if (t1) {
                this.finalInfoLinkToggleButton.setText("YES");
            } else {
                this.finalInfoLinkToggleButton.setText("NO");
            }
            syncModelView();
        });
        this.paginateToggleButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            this.stepData.setPaginate(t1);
            if (t1) {
                this.paginateToggleButton.setText("YES");
            } else {
                this.paginateToggleButton.setText("NO");
            }
            syncModelView();
        });
        this.pageTypeChoiceBox.setOnAction(actionEvent -> {
            PageTypeEnum pageType = this.pageTypeChoiceBox.getValue();
            if (null != pageType) {
                this.stepData.setPageType(this.pageTypeChoiceBox.getValue().getCode());
                syncModelView();
            }
        });
        this.urlParamView.setListener(value -> stepData.setUrl(value));
        this.labelParamView.setListener(value -> stepData.setLabel(value));
        this.pageUrlsParamView.setListener(value -> stepData.setPageUrls(value));
        this.nextPageLabelParamView.setListener(value -> stepData.setNextPageLabel(value));
        this.closeBtn.setOnMouseClicked(event ->{
            if (this.closeEvent != null) {
                this.closeEvent.handleClose(container);
            }
        });
        this.editMerchantBtn.setOnAction(event -> merchantInfoDialog.show());
    }

    public void renderView(CrawlerStepReq step, MerchantInfoModel merchantInfoReq) {
        this.stepData = step;
        this.urlField.setText(step.getUrl());
        this.finalInfoLinkToggleButton.setSelected(step.getFinalInfoLink() != null && step.getFinalInfoLink());
        this.labelField.setText(step.getLabel());
        this.paginateToggleButton.setSelected(step.getPaginate() != null && step.getPaginate());
        this.pageTypeChoiceBox.setValue(PageTypeEnum.typeOf(step.getPageType()));
        this.pageUrlsField.setText(step.getPageUrls());
        this.nextPageLabelField.setText(step.getNextPageLabel());

        this.merchantInfoDialog = new MerchantInfoDialog(merchantInfoReq);
    }

    public interface CloseEvent{
        void handleClose(VBox box);
    }

    public void setCloseEvent(CloseEvent closeEvent) {
        this.closeEvent = closeEvent;
    }

    private void syncModelView(){
        container.getChildren().clear();
        container.getChildren().add(topBar);
        container.getChildren().add(new Separator());
//        container.getChildren().add(this.closeBtn);
        container.getChildren().add(this.urlParamView.getRoot());
        container.getChildren().add(this.finalLinkInfoParamView.getRoot());
        if (this.stepData.getFinalInfoLink()==null || !this.stepData.getFinalInfoLink()) {
            container.getChildren().add(this.labelParamView.getRoot());
            container.getChildren().add(this.paginateParamView.getRoot());
            if (null != this.stepData.getPaginate() && this.stepData.getPaginate()) {
                container.getChildren().add(this.pageTypeParamView.getRoot());
                if (this.stepData.getPageType() == null || this.stepData.getPageType().equals(PageTypeEnum.NUMBER.getCode())) {
                    container.getChildren().add(this.pageUrlsParamView.getRoot());
                } else {
                    container.getChildren().add(this.nextPageLabelParamView.getRoot());
                }
            }
        } else {
            // 是信息页
            container.getChildren().add(this.editMerchantBtn);
        }

    }

    public CrawlerStepReq getStepData(){
        return this.stepData;
    }

    public VBox getRoot(){
        return container;
    }

    public MerchantInfoModel getMerchantInfo(){
        return this.merchantInfoDialog.getMerchantInfoModel();
    }
}
