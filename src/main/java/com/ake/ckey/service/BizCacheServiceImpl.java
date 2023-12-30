package com.ake.ckey.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ake.ckey.enums.TaskStatusEnum;
import com.ake.ckey.model.MerchantInfoModel;
import com.ake.ckey.model.TaskModel;
import org.jsoup.internal.StringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 11:56
 */
public class BizCacheServiceImpl extends BaseService implements BizCacheService {


    @Override
    public void cacheProperty(String key, Object data) {
        Path propertyPath = Paths.get(CACHE_PATH + PROPERTY_FILE);
        try {
            if (!Files.exists(propertyPath)) {
                Path parent = propertyPath.getParent();
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.createFile(propertyPath);
            }
            String dataStr = Files.readString(propertyPath);
            if (StringUtil.isBlank(dataStr)) {
                dataStr = "{}";
            }
            JSONObject jsonObject = JSONUtil.parseObj(dataStr);
            jsonObject.set(key, data);
            Files.writeString(propertyPath, jsonObject.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getProperty(String key) {
        Path propertyPath = Paths.get(CACHE_PATH + PROPERTY_FILE);
        try {
            if (!Files.exists(propertyPath)) {
                Path parent = propertyPath.getParent();
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                Files.createFile(propertyPath);
            }
            String dataStr = Files.readString(propertyPath);
            if (StringUtil.isBlank(dataStr)) {
                dataStr = "{}";
            }
            JSONObject jsonObject = JSONUtil.parseObj(dataStr);
            return jsonObject.get(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveCrawlerData(String url, List<MerchantInfoModel> datas) {
        String domain = getDomain(url);
        // 将网站的地址作为文件夹的名字
        String dataDir = DigestUtil.md5Hex(domain);
        Path dataPath = Paths.get(DATA_PATH + dataDir);
        try {
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            Stream<Path> fileList = Files.list(dataPath);
            List<String> fileNames = fileList.map(p -> p.getFileName().toString()).filter(fn -> fn.endsWith("dat")).collect(Collectors.toList());
            String latestFileName = "0000000000.dat";
            if (fileNames.size() > 0) {
                fileNames.sort(Comparator.naturalOrder());
                latestFileName = fileNames.get(fileNames.size() - 1);
            }
            Path finalPath = Paths.get(dataPath.toAbsolutePath().toString(), latestFileName);
            if (!Files.exists(finalPath)) {
                Files.createFile(finalPath);
            }
            long size = Files.size(finalPath);
            List<String> merchantStrList = datas.stream().map(JSONUtil::toJsonStr).collect(Collectors.toList());

            if (size < 300 * 1024 * 1024) {
                // 直接写入原来的文件中
                Files.write(finalPath, merchantStrList, StandardOpenOption.APPEND);
            } else {
                // 创建一个新的文件
                long ind = Long.parseLong(latestFileName.replace(".dat", ""));
                ind++;
                latestFileName = leftPad(String.valueOf(ind), 10, '0') + ".dat";
                finalPath = Paths.get(dataPath.toAbsolutePath().toString(), latestFileName);
                Files.createFile(finalPath);
                Files.write(finalPath, merchantStrList, StandardOpenOption.APPEND);
            }
            // 顺便保存企业的名字
            saveMerchantNames(Paths.get(DATA_PATH + dataDir + File.separator + MERCHANT_NAME_SET_FILE), datas.stream().map(MerchantInfoModel::getMerchantName).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getCrawledMerchantNames(String website) {
        String domain = getDomain(website);
        String dataDir = DigestUtil.md5Hex(domain);
        Path dataPath = Paths.get(DATA_PATH + dataDir + File.separator + MERCHANT_NAME_SET_FILE);
        if (!Files.exists(dataPath)) {
            return new HashSet<>();
        } else {
            try {
                String merchantNames = Files.readString(dataPath);
                return Arrays.stream(merchantNames.split(",")).distinct().collect(Collectors.toSet());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void updateTaskStatus(String taskName, TaskStatusEnum taskStatusEnum) {
        Object taskListObj = getProperty(TASK_LIST);
        if (null != taskListObj) {
            // 修改任务状态
            List<TaskModel> taskList = JSONUtil.toList((JSONArray) taskListObj, TaskModel.class);
            for (TaskModel model : taskList) {
                if (model.getTaskName().equals(taskName)) {
                    model.setStatus(taskStatusEnum.getDesc());
                    model.setStatusCode(taskStatusEnum.getCode());
                    cacheProperty(TASK_LIST, taskList);
                    break;
                }
            }
        }
    }

    private void saveMerchantNames(Path path, List<String> names) {
        if (CollectionUtil.isEmpty(names)) return;
        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            String data = names.stream().map(DigestUtil::md5Hex).collect(Collectors.joining(","));
            data = "," + data;
            Files.writeString(path, data, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
