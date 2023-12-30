package com.ake.ckey.model;

import lombok.Data;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 11:45
 */
@Data
public class TaskModel {

    private String taskUrl;
    private String taskName;

    private String startTime;

    private String finishedTime;

    private CrawlerReq reqData;

    private String status;

    private int statusCode;

    private long dataCount;
}
