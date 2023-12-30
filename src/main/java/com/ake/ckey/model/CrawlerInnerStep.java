package com.ake.ckey.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/11 16:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CrawlerInnerStep extends CrawlerStepReq{

    private String classId;

    private CrawlerInnerStep next;

    private String paginateClassId;
}
