package com.ake.ckey.model;

import lombok.Data;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/11 11:44
 */
@Data
public class CrawlerStepReq {

    private String label;

    private String url;

    private Boolean finalInfoLink;

    private Boolean paginate;

    private String pageType;

    private String pageUrls;

    private String nextPageLabel;

    private String stopStatus;

}
