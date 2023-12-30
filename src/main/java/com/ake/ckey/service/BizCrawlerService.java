package com.ake.ckey.service;

import com.ake.ckey.model.CrawlerReq;
import com.ake.ckey.model.MerchantInfoModel;
import javafx.scene.control.TableView;

import java.util.concurrent.Future;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/21 19:34
 */
public interface BizCrawlerService {

    Future<?> crawlData(CrawlerReq reqData);

    void setDataTable(TableView<MerchantInfoModel> dataTable);
}
