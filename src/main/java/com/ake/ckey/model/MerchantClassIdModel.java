package com.ake.ckey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/11 16:58
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantClassIdModel extends MerchantInfoModel{

    private String merchantNameClassId;

    private String establishDateClassId;

    private String merchantBusinessLicenseCodeClassId;

    private String merchantMobilePhoneClassId;

    private String merchantProvinceClassId;

    private String registeredCapitalClassId;

    private String merchantCityClassId;

    private String merchantAreaClassId;

    private String merchantAddressClassId;

    private String merchantEmailClassId;

    private String companyCategoryClassId;

    private String merchantIndustryLargeCategoryClassId;

    private String businessScopeClassId;

    private String legalRepresentativeClassId;

    private String bizCodeClassId;

    private String countryCodeClassId;

    private String merchantLogoClassId;

    private String merchantWebsiteClassId;

    private String merchantCountryClassId;

    private String postInfoClassId;

    private String sourceClassId;

    private String merchantImageClassId;
}
