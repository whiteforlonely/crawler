package com.ake.ckey.model;

import lombok.Data;

import java.util.List;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/6 16:03
 */
@Data
public class CrawlerReq {

    private String userName;

    private String url;

    private String languageCode="en-US";

    private String country;

    private List<CrawlerStepReq> steps;

    private MerchantInfoModel merchantInfoReq;
}
