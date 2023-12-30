package com.ake.ckey.view;

import com.ake.ckey.model.MerchantInfoModel;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.jsoup.internal.StringUtil;

import java.util.Optional;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 9:03
 */
public class MerchantInfoDialog {

    private Alert alert;

    private MerchantInfoModel merchantInfoModel;
    private GridPane infoPane;

    private TextField merchantNameField;
    private TextField merchantWebsiteField;
    private TextField merchantCategoryField;
    private TextField merchantCountryField;
    private TextField merchantProvinceField;
    private TextField merchantCityField;
    private TextField merchantAddrField;
    private TextField merchantPhoneField;
    private TextField merchantEmailField;
    private TextField merchantEstablishField;
    private TextField merchantLicenseCodeField;
    private TextField merchantDescField;
    private TextField merchantBizCodeField;
    private TextField merchantLogoField;
    private TextField merchantImageField;
    private TextField merchantPostField;
    private TextField merchantCapitalField;

    public MerchantInfoDialog(MerchantInfoModel merchantInfoModel){
        if (merchantInfoModel == null) {
            merchantInfoModel = new MerchantInfoModel();
        }
        this.merchantInfoModel = merchantInfoModel;
        this.alert = new Alert(Alert.AlertType.CONFIRMATION);
        this.alert.setTitle("填入页面企业信息");
        this.alert.setHeaderText("这边将最终信息页中的包含的企业信息都录入保存下来");

        this.merchantNameField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantName())) {
            this.merchantNameField.setText(merchantInfoModel.getMerchantName());
        }
        this.merchantWebsiteField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantWebsite())) {
            this.merchantWebsiteField.setText(merchantInfoModel.getMerchantWebsite());
        }
        this.merchantCategoryField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantIndustryLargeCategory())) {
            this.merchantCategoryField.setText(merchantInfoModel.getMerchantIndustryLargeCategory());
        }
        this.merchantCountryField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantCountry())) {
            this.merchantCountryField.setText(merchantInfoModel.getMerchantCountry());
        }
        this.merchantProvinceField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantProvince())) {
            this.merchantProvinceField.setText(merchantInfoModel.getMerchantProvince());
        }
        this.merchantCityField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantCity())) {
            this.merchantCityField.setText(merchantInfoModel.getMerchantCity());
        }
        this.merchantAddrField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantAddress())) {
            this.merchantAddrField.setText(merchantInfoModel.getMerchantAddress());
        }
        this.merchantPhoneField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantMobilePhone())) {
            this.merchantPhoneField.setText(merchantInfoModel.getMerchantMobilePhone());
        }

        this.merchantEmailField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantEmail())) {
            this.merchantEmailField.setText(merchantInfoModel.getMerchantEmail());
        }
        this.merchantEstablishField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getEstablishDate())) {
            this.merchantEstablishField.setText(merchantInfoModel.getEstablishDate());
        }
        this.merchantLicenseCodeField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantBusinessLicenseCode())) {
            this.merchantLicenseCodeField.setText(merchantInfoModel.getMerchantBusinessLicenseCode());
        }
        this.merchantDescField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getBusinessScope())) {
            this.merchantDescField.setText(merchantInfoModel.getBusinessScope());
        }
        this.merchantBizCodeField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getBizCode())) {
            this.merchantBizCodeField.setText(merchantInfoModel.getBizCode());
        }
        this.merchantLogoField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantLogo())) {
            this.merchantLogoField.setText(merchantInfoModel.getMerchantLogo());
        }
        this.merchantImageField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getMerchantImage())) {
            this.merchantImageField.setText(merchantInfoModel.getMerchantImage());
        }
        this.merchantPostField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getPostInfo())) {
            this.merchantPostField.setText(merchantInfoModel.getPostInfo());
        }
        this.merchantCapitalField = new TextField();
        if (!StringUtil.isBlank(merchantInfoModel.getRegisteredCapital())) {
            this.merchantCapitalField.setText(merchantInfoModel.getRegisteredCapital());
        }

        infoPane = new GridPane();
        infoPane.addColumn(0,
                new Label("企业名称"),
                new Label("官网"),
                new Label("行业类别"),
                new Label("国家"),
                new Label("省份"),
                new Label("城市"),
                new Label("地址"),
                new Label("电话"),
                new Label("邮箱")
                );
        infoPane.addColumn(1,
                this.merchantNameField,
                this.merchantWebsiteField,
                this.merchantCategoryField,
                this.merchantCountryField,
                this.merchantProvinceField,
                this.merchantCityField,
                this.merchantAddrField,
                this.merchantPhoneField,
                this.merchantEmailField
//                this.merchantEstablishField,
//                this.merchantLicenseCodeField,
//                this.merchantDescField,
//                this.merchantBizCodeField,
//                this.merchantLogoField,
//                this.merchantImageField,
//                this.merchantPostField,
//                this.merchantCapitalField
                );
        infoPane.addColumn(2,
//                new Label("企业名称"),
//                new Label("官网"),
//                new Label("行业类别"),
//                new Label("国家"),
//                new Label("省份"),
//                new Label("城市"),
//                new Label("地址"),
//                new Label("电话"),
//                new Label("邮箱"),
                new Label("成立时间"),
                new Label("统一社会信用号"),
                new Label("企业简介"),
                new Label("行业代码"),
                new Label("商标"),
                new Label("宣传图"),
                new Label("邮编"),
                new Label("注册资产"));
        infoPane.addColumn(3,
//                this.merchantNameField,
//                this.merchantWebsiteField,
//                this.merchantCategoryField,
//                this.merchantCountryField,
//                this.merchantProvinceField,
//                this.merchantCityField,
//                this.merchantAddrField,
//                this.merchantPhoneField,
//                this.merchantEmailField
                this.merchantEstablishField,
                this.merchantLicenseCodeField,
                this.merchantDescField,
                this.merchantBizCodeField,
                this.merchantLogoField,
                this.merchantImageField,
                this.merchantPostField,
                this.merchantCapitalField
        );
        infoPane.setHgap(20);
        infoPane.setVgap(10);
        infoPane.setPadding(new Insets(20));
        this.alert.getDialogPane().setContent(infoPane);
    }

    public void show(){
        Optional<ButtonType> buttonType = this.alert.showAndWait();
        if (buttonType.get() == ButtonType.OK) {
            // 开始赋值
            this.merchantInfoModel.setMerchantName(this.merchantNameField.getText());
            this.merchantInfoModel.setMerchantWebsite(this.merchantWebsiteField.getText());
            this.merchantInfoModel.setMerchantIndustryLargeCategory(this.merchantCategoryField.getText());
            this.merchantInfoModel.setMerchantCountry(this.merchantCountryField.getText());
            this.merchantInfoModel.setMerchantProvince(this.merchantProvinceField.getText());
            this.merchantInfoModel.setMerchantCity(this.merchantCityField.getText());
            this.merchantInfoModel.setMerchantAddress(this.merchantAddrField.getText());
            this.merchantInfoModel.setMerchantMobilePhone(this.merchantPhoneField.getText());
            this.merchantInfoModel.setMerchantEmail(this.merchantEmailField.getText());
            this.merchantInfoModel.setEstablishDate(this.merchantEstablishField.getText());
            this.merchantInfoModel.setMerchantBusinessLicenseCode(this.merchantLicenseCodeField.getText());
            this.merchantInfoModel.setBusinessScope(this.merchantDescField.getText());
            this.merchantInfoModel.setBizCode(this.merchantBizCodeField.getText());
            this.merchantInfoModel.setMerchantLogo(this.merchantLogoField.getText());
            this.merchantInfoModel.setMerchantImage(this.merchantImageField.getText());
            this.merchantInfoModel.setPostInfo(this.merchantPostField.getText());
            this.merchantInfoModel.setRegisteredCapital(this.merchantCapitalField.getText());
        }
//        else {
//            this.merchantNameField.setText("");
//            this.merchantWebsiteField.setText("");
//            this.merchantCategoryField.setText("");
//            this.merchantCountryField.setText("");
//            this.merchantProvinceField.setText("");
//            this.merchantCityField.setText("");
//            this.merchantAddrField.setText("");
//            this.merchantPhoneField.setText("");
//            this.merchantEmailField.setText("");
//            this.merchantEstablishField.setText("");
//            this.merchantLicenseCodeField.setText("");
//            this.merchantDescField.setText("");
//            this.merchantBizCodeField.setText("");
//            this.merchantLogoField.setText("");
//            this.merchantImageField.setText("");
//            this.merchantPostField.setText("");
//            this.merchantCapitalField.setText("");
//        }
        this.alert.hide();
    }

    public MerchantInfoModel getMerchantInfoModel(){
        return this.merchantInfoModel;
    }
}
