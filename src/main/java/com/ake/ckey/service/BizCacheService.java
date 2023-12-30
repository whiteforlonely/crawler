package com.ake.ckey.service;

import com.ake.ckey.enums.TaskStatusEnum;
import com.ake.ckey.model.MerchantInfoModel;

import java.util.List;
import java.util.Set;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 11:37
 */
public interface BizCacheService {

    String CACHE_PATH = "./crawler/cache/";
    String PROPERTY_FILE = "ckey_crawler.properties";
    String DATA_PATH = "./crawler/data/";

    String TASK_LIST="task_list";
    String MERCHANT_NAME_SET_FILE="merchant_name.set";

    // 保存属性值
    void cacheProperty(String key, Object data);

    // 获取属性值
    Object getProperty(String key);

    // 保存爬取的数据
    void saveCrawlerData(String url, List<MerchantInfoModel> datas);

    // 获取已经爬取的企业数据
    Set<String> getCrawledMerchantNames(String website);

    void updateTaskStatus(String taskName, TaskStatusEnum taskStatus);
}
