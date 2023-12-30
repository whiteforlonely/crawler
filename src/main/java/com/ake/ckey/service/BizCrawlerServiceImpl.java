package com.ake.ckey.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.ake.ckey.callback.CounterRemindCallback;
import com.ake.ckey.callback.LogCallback;
import com.ake.ckey.callback.ReadyCallback;
import com.ake.ckey.callback.TaskStatusCallback;
import com.ake.ckey.controller.AlertController;
import com.ake.ckey.enums.TaskStatusEnum;
import com.ake.ckey.model.*;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/6 15:46
 */
public class BizCrawlerServiceImpl extends BaseService implements BizCrawlerService {

    private static AtomicInteger taskCounter = new AtomicInteger(0);
    private AtomicLong dataCounter = new AtomicLong(0);

    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private TableView<MerchantInfoModel> dataTable;
    private List<MerchantInfoModel> dataList = new ArrayList<>();
    private BizCacheService cacheService = new BizCacheServiceImpl();
    private CrawlerReq crawlerReq;

    private CounterRemindCallback counterRemindCallback;
    private TaskStatusCallback taskStatusCallback;
    private Set<String> merchantNameSet = new HashSet<>();
    private ReadyCallback readyCallback;
    private LogCallback logCallback;

    private volatile boolean stopTag = false;


    public void setLogCallback(LogCallback logCallback) {
        this.logCallback = logCallback;
    }

    public void setReadyCallback(ReadyCallback readyCallback) {
        this.readyCallback = readyCallback;
    }

    public void setCounterRemindCallback(CounterRemindCallback counterRemindCallback) {
        this.counterRemindCallback = counterRemindCallback;
    }

    public void setTaskStatusCallback(TaskStatusCallback taskStatusCallback) {
        this.taskStatusCallback = taskStatusCallback;
    }

    public void setStopTag(boolean stopTag) {
        this.stopTag = stopTag;
    }

    private enum ValueType {
        TEXT,
        IMAGE,
        LINK,
    }

    @Override
    public Future crawlData(CrawlerReq reqData) {
        this.crawlerReq = reqData;
        // 1. 解析主页数据
        int totalCount = taskCounter.getAndIncrement();
        log("任务数量： " + totalCount);
        if (totalCount > 100) {
            if (null != taskStatusCallback) {
                taskStatusCallback.handleTaskStatus(TaskStatusEnum.READY);
            }
            AlertController.exception("任务超出限制", "您的爬虫任务已经超过5个，请停止掉或者关闭掉其他任务再重新开始。");
            taskCounter.addAndGet(-1);
            return null;
        }
        try {
            return handleStepOfPage(reqData);
        } catch (IOException e) {
            if (null != this.taskStatusCallback) {
                this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
            }
            AlertController.exception("爬取数据错误", e.toString());
            taskCounter.addAndGet(-1);
        }
        return null;
    }

    @Override
    public void setDataTable(TableView<MerchantInfoModel> dataTable) {
        this.dataTable = dataTable;
    }

    private Document getDoc(String url) throws IOException {
//        Document doc = Jsoup.parse(renderHtml(url).asXml());
        return Jsoup.connect(url).get();
    }

    private Future<?> handleStepOfPage(CrawlerReq reqData) throws IOException {

        log("开始解析首页数据： " + reqData.getUrl());
        String domain = getDomain(reqData.getUrl());

        List<CrawlerInnerStep> innerSteps = reqData.getSteps().stream().map(t -> {
            CrawlerInnerStep step = new CrawlerInnerStep();
            BeanUtil.copyProperties(t, step);
            return step;
        }).collect(Collectors.toList());

        if (innerSteps.size() == 0) {
            taskCounter.addAndGet(-1);
            if (null != this.taskStatusCallback) {
                this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
            }
            AlertController.exception("爬取数据错误", "请添加爬取数据步骤！");
            return null;
        }

        log("开始分析轨迹数据 ...");
        for (CrawlerInnerStep step : innerSteps) {
            // 跟踪每一步，并且对每一步的数据配置成对应的class标签
            log("轨迹地址： " + step.getUrl() +", 轨迹标签： " + step.getLabel());
            if (null != step.getFinalInfoLink() && step.getFinalInfoLink()) {
                break;
            }
            if (StringUtil.isBlank(step.getUrl())) {
                taskCounter.addAndGet(-1);
                if (null != this.taskStatusCallback) {
                    this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
                }
                AlertController.exception("爬取数据错误", "步骤中的URL不为空");
                return null;
            }
            Document currDoc = getDoc(step.getUrl());
            log("轨迹页面解析结果： " + currDoc.location());
            Elements els = currDoc.getElementsContainingOwnText(step.getLabel());
            log("包含文本【" + step.getLabel() +"】的元素个数： " + els.size());
            if (els.size() == 0) {
                taskCounter.addAndGet(-1);
                if (null != this.taskStatusCallback) {
                    this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
                }
                AlertController.exception("爬取数据错误", "在【" + step.getUrl() + "】中未找到对应的文本【"+ step.getLabel() + "】信息");
                return null;
            }
            int i = 0;
            Element el = els.get(i++);
            // 找到内容完全相同的标签
            while(!step.getLabel().trim().equals(el.text().trim()) && i < els.size()) {
                el = els.get(i++);
            }
            String elClass = el.attr("class");
            log("找到对应的样式： " + elClass);
            while (StringUtil.isBlank(elClass) || el.getElementsByTag("a").size() == 0) {
                el = el.parent();
                if (el == null) break;
                elClass = el.attr("class");
                log("重新找到的父级样式： " + elClass);
            }

            // 针对于分页的数据进行处理
            if (null != step.getPaginate() && step.getPaginate() && (StringUtil.isBlank(step.getPageType()) || "pageNum".equals(step.getPageType()))) {
                // 需要进行分页，需要查找出分页的相关规律
                log("对页码分页数据进行解析...");
                String pageLinkText = step.getPageUrls().replace(domain, "");
                Elements pageALink = currDoc.getElementsByAttributeValueContaining("href", pageLinkText);
                if (pageALink.size() == 0) {
                    taskCounter.addAndGet(-1);
                    if (null != this.taskStatusCallback) {
                        this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
                    }
                    AlertController.exception("轨迹数据错误", "下一页的地址必须和当前页是不一样的，请对其中有分页数据的信息进行重新编辑");
                    return null;
                }
                List<Element> collect = pageALink.stream().filter(e -> "a".equals(e.tagName())).collect(Collectors.toList());
                if (collect.size() > 0) {
                    Element element = collect.get(0);
                    String pageClassName = "";
                    do {
                        pageClassName = element.attr("class");
                        element = element.parent();
                    } while (null != element && (StringUtil.isBlank(pageClassName) || element.getElementsByTag("a").size() == 0));
                    step.setPaginateClassId(pageClassName);
                }
                if (StringUtil.isBlank(step.getPaginateClassId())) {
                    taskCounter.addAndGet(-1);
                    if (null != this.taskStatusCallback) {
                        this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
                    }
                    AlertController.exception("分页数据解析失败", "分页数据步骤中的classID为空");
                    return null;
                }
                log("包含页面的标签是 " + elClass);
            }
            step.setClassId(elClass);
        }

        // 将所有步骤连接起来
        for (int i = 0; i < innerSteps.size(); i++) {
            if (i < innerSteps.size()-1) {
                innerSteps.get(i).setNext(innerSteps.get(i+1));
            }
        }

        log("解析信息页对应的标签");
        // 解析最终页面对应的字段class数据，
        MerchantClassIdModel merchantClassIdModel =new MerchantClassIdModel();
        BeanUtil.copyProperties(reqData.getMerchantInfoReq(), merchantClassIdModel);
        CrawlerInnerStep lastStep = innerSteps.get(innerSteps.size() - 1);
        initClassIdModel(merchantClassIdModel, lastStep);


        // 开始按步骤爬取数据
        Future<?> taskFuture = executorService.submit(() -> {
            Document firstDoc;
            merchantNameSet = cacheService.getCrawledMerchantNames(this.crawlerReq.getUrl());
            dataCounter = new AtomicLong(merchantNameSet.size());
            try {
                log("开始解析所网站数据");
                firstDoc = getDoc(innerSteps.get(0).getUrl());
                if (null != this.readyCallback) {
                    this.readyCallback.handleReady();
                }
                handleStep(firstDoc, innerSteps.get(0), merchantClassIdModel);
                cacheService.updateTaskStatus(DigestUtil.md5Hex(this.crawlerReq.getUrl()), TaskStatusEnum.DONE);
                if (null != this.taskStatusCallback) {
                    this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.DONE);
                }
            } catch (IOException e) {
                System.out.println("exception finally: " + e);
                cacheService.updateTaskStatus(DigestUtil.md5Hex(this.crawlerReq.getUrl()), TaskStatusEnum.EXCEPTION);
                if (null != this.taskStatusCallback) {
                    this.taskStatusCallback.handleTaskStatus(TaskStatusEnum.EXCEPTION);
                }
                AlertController.exception("轨迹数据错误", e.toString());
            } finally {
                taskCounter.addAndGet(-1);
            }
        });
        return taskFuture;
    }

    private void log(String text) {
        if (null != this.logCallback) {
            this.logCallback.log(text);
        }
    }

    /**
     * 初始化最终数据中的class css数据
     */
    private void initClassIdModel(MerchantClassIdModel merchantClassIdModel, CrawlerInnerStep lastStep) throws IOException {
        Document document = getDoc(lastStep.getUrl());
        String domain = getDomain(lastStep.getUrl());
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantName())) {
            merchantClassIdModel.setMerchantNameClassId(getFieldClassId(document, merchantClassIdModel.getMerchantName()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getBizCode())) {
            merchantClassIdModel.setBizCodeClassId(getFieldClassId(document, merchantClassIdModel.getBizCode()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getBusinessScope())) {
            merchantClassIdModel.setBusinessScopeClassId(getFieldClassId(document, merchantClassIdModel.getBusinessScope()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getCompanyCategory())) {
            merchantClassIdModel.setCompanyCategoryClassId(getFieldClassId(document, merchantClassIdModel.getCompanyCategory()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getCountryCode())) {
            merchantClassIdModel.setCountryCodeClassId(getFieldClassId(document, merchantClassIdModel.getCountryCode()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getEstablishDate())) {
            merchantClassIdModel.setEstablishDateClassId(getFieldClassId(document, merchantClassIdModel.getEstablishDate()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantAddress())) {
            merchantClassIdModel.setMerchantAddressClassId(getFieldClassId(document, merchantClassIdModel.getMerchantAddress()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getLegalRepresentative())) {
            merchantClassIdModel.setLegalRepresentativeClassId(getFieldClassId(document, merchantClassIdModel.getLegalRepresentative()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantCity())) {
            merchantClassIdModel.setMerchantCityClassId(getFieldClassId(document, merchantClassIdModel.getMerchantCity()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantArea())) {
            merchantClassIdModel.setMerchantAreaClassId(getFieldClassId(document, merchantClassIdModel.getMerchantArea()));
        }
        //*******************************************************************
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantCountry())) {
            merchantClassIdModel.setMerchantCountryClassId(getFieldClassId(document, merchantClassIdModel.getMerchantCountry()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantEmail())) {
            merchantClassIdModel.setMerchantEmailClassId(getFieldClassId(document, merchantClassIdModel.getMerchantEmail()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantMobilePhone())) {
            merchantClassIdModel.setMerchantMobilePhoneClassId(getFieldClassId(document, merchantClassIdModel.getMerchantMobilePhone()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantBusinessLicenseCode())) {
            merchantClassIdModel.setMerchantBusinessLicenseCodeClassId(getFieldClassId(document, merchantClassIdModel.getMerchantBusinessLicenseCode()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantImage())) {
            merchantClassIdModel.setMerchantImageClassId(getFieldClassId(document, merchantClassIdModel.getMerchantImage().replace(domain, ""), ValueType.IMAGE));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantLogo())) {
            merchantClassIdModel.setMerchantLogoClassId(getFieldClassId(document, merchantClassIdModel.getMerchantLogo().replace(domain, ""), ValueType.IMAGE));
        }
        //*******************************************************************
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantIndustryLargeCategory())) {
            merchantClassIdModel.setMerchantIndustryLargeCategoryClassId(getFieldClassId(document, merchantClassIdModel.getMerchantIndustryLargeCategory()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantWebsite())) {
            merchantClassIdModel.setMerchantWebsiteClassId(getFieldClassId(document, merchantClassIdModel.getMerchantWebsite().replace(domain, ""), ValueType.LINK));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantProvince())) {
            merchantClassIdModel.setMerchantProvinceClassId(getFieldClassId(document, merchantClassIdModel.getMerchantProvince()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getPostInfo())) {
            merchantClassIdModel.setPostInfoClassId(getFieldClassId(document, merchantClassIdModel.getPostInfo()));
        }
        //*******************************************************************
        if (!StringUtil.isBlank(merchantClassIdModel.getRegisteredCapital())) {
            merchantClassIdModel.setRegisteredCapitalClassId(getFieldClassId(document, merchantClassIdModel.getRegisteredCapital()));
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getSource())) {
            merchantClassIdModel.setSourceClassId(getFieldClassId(document, merchantClassIdModel.getSource()));
        }
    }

    private String getFieldClassId(Document document, String value, ValueType valueType){
        Elements els = document.getElementsContainingOwnText(value);

        if (valueType == ValueType.IMAGE) {
            // 图片数据
            els = document.getElementsByAttributeValueContaining("src", value);
        } else if (valueType == ValueType.LINK) {
            els = document.getElementsByAttributeValueContaining("href", value);
        }
        if (els.size() > 0) {
            // 需要找出完全匹配的
            Element el = els.get(0);
//            for (Element tmpEl : els) {
//                if (tmpEl.text().trim().equals(value.trim())) {
//                    el = tmpEl;
//                    break;
//                }
//            }

            List<String> tagClassList = new ArrayList<>();
            // 找出所有父级的标签和对应的class还有对应的索引，必须从下到上去查找，才能找到正确位置
            do {
                String elClass = !StringUtil.isBlank(el.className()) ? el.className() : !StringUtil.isBlank(el.id()) ? el.id() : "";
                Element parent = el.parent();
                if (parent == null) break;
                Elements children = parent.children();
                for (int j = 0; j < children.size(); j++) {
                    Element element = children.get(j);
                    if (el.text().equals(element.text())) {
                        String tagClass = el.tagName() + (StringUtil.isBlank(elClass) ? "/ " : "/" + elClass) + "/" + j;
                        tagClassList.add(tagClass);
                        break;
                    }
                }
            } while (null != (el = el.parent()) && !el.tagName().equals("html"));

            // 倒序下
            Collections.reverse(tagClassList);

            // 编码数据位置
            return String.join("//", tagClassList);
        }
        return null;
    }

    private String getFieldClassId(Document document, String value){
        Elements els = document.getElementsContainingOwnText(value);
        log("解析企业信息页数据： " + value);
        if (els.size() > 0) {
            // 需要找出完全匹配的
            Element el = els.get(0);
            for (Element tmpEl : els) {
                if (tmpEl.text().trim().equals(value.trim())) {
                    el = tmpEl;
                    break;
                }
            }

            List<String> tagClassList = new ArrayList<>();
            // 找出所有父级的标签和对应的class还有对应的索引，必须从下到上去查找，才能找到正确位置
            do {
                String elClass = !StringUtil.isBlank(el.className()) ? el.className() : !StringUtil.isBlank(el.id()) ? el.id() : "";
                Element parent = el.parent();
                if (parent == null) break;
                Elements children = parent.children();
                for (int j = 0; j < children.size(); j++) {
                    Element element = children.get(j);
                    if (el.text().equals(element.text())) {
                        String tagClass = el.tagName() + (StringUtil.isBlank(elClass) ? "/ " : "/" + elClass) + "/" + j;
                        tagClassList.add(tagClass);
                        log("数据标签： " + tagClass);
                        break;
                    }
                }
            } while (null != (el = el.parent()) && !el.tagName().equals("html"));

            // 倒序下
            Collections.reverse(tagClassList);
            // 编码数据位置
            return String.join("//", tagClassList);
        }
        return null;
    }

    private String getNextPageLink(String domain, Document doc, String nextPageLabel){
        Elements nextPageEls = doc.getElementsContainingOwnText(nextPageLabel);
        if (nextPageEls.size() > 0) {
            for (Element nextPageEl : nextPageEls) {
                if (nextPageLabel.equals(nextPageEl.text().trim())) {
                    do {
                        String link = nextPageEl.attr("href");
                        if (!StringUtil.isBlank(link)) {
                            return optimizeUrl(domain, link);
                        }
                    } while ((nextPageEl = nextPageEl.parent()) != null);
                    break;
                }
            }
        }
        return "";
    }

    private void handleStep(Document doc, CrawlerInnerStep crawlerStepReq, MerchantClassIdModel merchantClassIdModel) throws IOException {
        log("解析页面： " + doc.location());
        String domain = getDomain(crawlerStepReq.getUrl());
        if (null != crawlerStepReq.getNext()) {
            log("解析中间也信息...");
            if (null != crawlerStepReq.getPaginate() && crawlerStepReq.getPaginate()) {
                if (!StringUtil.isBlank(crawlerStepReq.getPageType()) && "pre-next".equals(crawlerStepReq.getPageType())) {
//                    Elements pageEls = doc.getElementsContainingOwnText(crawlerStepReq.getPaginateClassId());
                    // 现处理当前页
                    handleStepData(doc, crawlerStepReq, merchantClassIdModel);
                    String nextLink = getNextPageLink(domain, doc, crawlerStepReq.getNextPageLabel());
                    // 处理下一页
                    while (!StringUtil.isBlank(nextLink)) {
                        log("解析下一页数据： " + nextLink);
                        // 开始遍历分页数据
                        doc = getDoc(nextLink);
                        handleStepData(doc, crawlerStepReq, merchantClassIdModel);
                        nextLink = getNextPageLink(domain, doc, crawlerStepReq.getNextPageLabel());
                    }
                } else {
                    // 需要处理有标号的分页数据
                    Elements pageEls = doc.getElementsByClass(crawlerStepReq.getPaginateClassId());
                    if (pageEls.size() > 0) {
                        Elements aEls = pageEls.get(0).getElementsByTag("a");
                        List<String> hrefs = aEls.stream().map(a -> a.attr("href")).distinct().collect(Collectors.toList());
                        if (hrefs.size() > 1) {
                            String href1 = hrefs.get(0);
                            String href2 = hrefs.get(1);
                            // 找出两个连接中的差异，并找出最大的分页数量
                            int indPre = 0;
                            int indAfter = 0;
                            boolean startDiff = false;
                            for (int i = 0; i < href1.toCharArray().length; i++) {
                                char c = href2.charAt(i);
                                if (href1.charAt(i) == c) {
                                    if (startDiff) {
                                        if (indAfter == 0) {
                                            indAfter = i;
                                        }
                                    } else {
                                        indPre++;
                                    }
                                } else {
                                    startDiff = true;
                                }
                            }

                            String prefix = href1.substring(0, indPre);
                            String suffix = href1.substring(indAfter);
                            // 找到最大的页码
                            String maxHref = hrefs.get(hrefs.size() - 1);
                            String maxPageStr = maxHref.replace(prefix, "").replace(suffix, "");
                            int maxPage = Integer.parseInt(maxPageStr);
                            for (int i = 1; i <= maxPage; i++) {
                                // 对分页数据进行遍历
                                String url = prefix + i + suffix;
                                url = optimizeUrl(domain, url);

//                                if (!StringUtil.isBlank(crawlerStepReq.getStopStatus())) {
//                                    if (!url.equals(crawlerStepReq.getStopStatus())) {
//                                        continue;
//                                    } else {
//                                        crawlerStepReq.setStopStatus(url);
//                                        syncStepStatus(crawlerStepReq);
//                                        crawlerStepReq.setStopStatus(null);
//                                    }
//                                } else {
//                                    // 同步状态
//                                    crawlerStepReq.setStopStatus(url);
//                                    syncStepStatus(crawlerStepReq);
//                                    crawlerStepReq.setStopStatus(null);
//                                }

                                try {
                                    log("解析下一页数据： " + url);
                                    doc = getDoc(url);
                                    handleStepData(doc, crawlerStepReq, merchantClassIdModel);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    if (this.stopTag) {
                                        // 记录爬取状态
                                        throw new RuntimeException("stop application");
                                    }
                                }
                            }
                        } else {
                            handleStepData(doc, crawlerStepReq, merchantClassIdModel);
                        }
                    }
                }
            } else {
                handleStepData(doc, crawlerStepReq, merchantClassIdModel);
            }

        } else {
            handleData(doc, merchantClassIdModel, domain);
        }
    }

    private void syncStepStatus(CrawlerInnerStep step){
        log("[syncStepStatus] 同步跑去状态： " + step.getStopStatus());
        for (CrawlerStepReq crawlerReqStep : this.crawlerReq.getSteps()) {
            if (step.getUrl().equals(crawlerReqStep.getUrl()) && step.getLabel().equals(crawlerReqStep.getLabel())) {
                crawlerReqStep.setStopStatus(step.getStopStatus());
                break;
            }
        }
    }

    private String optimizeUrl(String domain, String url) {
        if (url.contains("%20")) {
            System.out.println("debugger ....");
        }
        if (StringUtil.isBlank(url)) return url;
        if (!url.contains("http")) {
            if (url.startsWith("//")) {
                url = url.substring(2);
            } else if (url.startsWith("/")) {
                url = url.substring(1);
            }
            url = domain + url;
        }
        if (url.contains("../")) {
            url = url.replaceAll("\\.\\./", "");
        }
        return url;
    }

    private void handleStepData(Document doc, CrawlerInnerStep crawlerStepReq, MerchantClassIdModel merchantClassIdModel){
        String domain = getDomain(crawlerStepReq.getUrl());
        Elements els = doc.getElementsByClass(crawlerStepReq.getClassId());
        log("[handleStepData] 包含class=【"+crawlerStepReq.getClassId()+"】的标签有【"+els.size()+"】个");
        if (els.size() > 0) {
            loop: for (Element el : els) {
                Elements aEls = el.getElementsByTag("a");
                if (aEls.size() > 0) {
                    for (Element aEl : aEls) {
                        String aHref = aEl.attr("href");
                        aHref = optimizeUrl(domain, aHref);
                        log("[handleStepData] 开始解析【"+aHref+"】");
                        try {
                            if (!StringUtil.isBlank(crawlerStepReq.getStopStatus())) {
                                if (!aHref.equals(crawlerStepReq.getStopStatus())) {
                                    continue;
                                } else {
                                    crawlerStepReq.setStopStatus(aHref);
                                    syncStepStatus(crawlerStepReq);
                                    crawlerStepReq.setStopStatus(null);
                                }
                            } else {
                                // 同步状态
                                crawlerStepReq.setStopStatus(aHref);
                                syncStepStatus(crawlerStepReq);
                                crawlerStepReq.setStopStatus(null);
                            }
                            // 开始爬取数据
                            Document document = getDoc(aHref);
                            handleStep(document, crawlerStepReq.getNext(), merchantClassIdModel);
                        } catch (IOException e) {
                            // 需要去掉抛出异常，有些链接是有问题的
//                            e.printStackTrace();
                            e.printStackTrace();
                        }
                        // break template, 后面要注释掉
//                        break loop;
                    }
                }
            }
        }
    }

    private void handleData(Document doc, MerchantClassIdModel merchantClassIdModel, String domain){
        if (this.stopTag) {
            throw new RuntimeException("task stop exception");
        }
        MerchantInfoModel infoModel = new MerchantInfoModel();
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantNameClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantNameClassId(), MerchantInfoModel::setMerchantName);
            if (StringUtil.isBlank(infoModel.getMerchantName())) {
                return;
            }
            String merchantNameMd5 = DigestUtil.md5Hex(infoModel.getMerchantName());
            if (merchantNameSet.contains(merchantNameMd5)) {
                System.out.println("------------repeat-----------" + infoModel.getMerchantName());
                return;
            }
            merchantNameSet.add(merchantNameMd5);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getEstablishDateClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getEstablishDateClassId(), MerchantInfoModel::setEstablishDate);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantEmailClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantEmailClassId(), MerchantInfoModel::setMerchantEmail);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantMobilePhoneClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantMobilePhoneClassId(), MerchantInfoModel::setMerchantMobilePhone);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getBusinessScopeClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getBusinessScopeClassId(), MerchantInfoModel::setBusinessScope);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantImageClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantImageClassId(), MerchantInfoModel::setMerchantImage, ValueType.IMAGE, domain);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantLogoClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantLogoClassId(), MerchantInfoModel::setMerchantLogo, ValueType.IMAGE, domain);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantCountryClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantCountryClassId(), MerchantInfoModel::setMerchantCountry);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantProvinceClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantProvinceClassId(), MerchantInfoModel::setMerchantProvince);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantCityClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantCityClassId(), MerchantInfoModel::setMerchantCity);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantAreaClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantAreaClassId(), MerchantInfoModel::setMerchantArea);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getCountryCodeClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getCountryCodeClassId(), MerchantInfoModel::setCountryCode);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantAddressClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantAddressClassId(), MerchantInfoModel::setMerchantAddress);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantWebsiteClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantWebsiteClassId(), MerchantInfoModel::setMerchantWebsite, ValueType.LINK, domain);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getBizCodeClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getBizCodeClassId(), MerchantInfoModel::setBizCode);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantBusinessLicenseCodeClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantBusinessLicenseCodeClassId(), MerchantInfoModel::setMerchantBusinessLicenseCode);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getMerchantIndustryLargeCategoryClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getMerchantIndustryLargeCategoryClassId(), MerchantInfoModel::setMerchantIndustryLargeCategory);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getCompanyCategoryClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getCompanyCategoryClassId(), MerchantInfoModel::setCompanyCategory);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getSourceClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getSourceClassId(), MerchantInfoModel::setSource);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getLegalRepresentativeClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getLegalRepresentativeClassId(), MerchantInfoModel::setLegalRepresentative);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getRegisteredCapitalClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getRegisteredCapitalClassId(), MerchantInfoModel::setRegisteredCapital);
        }
        if (!StringUtil.isBlank(merchantClassIdModel.getPostInfoClassId())) {
            getClassLabel(doc, infoModel, merchantClassIdModel.getPostInfoClassId(), MerchantInfoModel::setPostInfo);
        }

        System.out.println("craw merchant name = " + infoModel.getMerchantName());

        // 开始保存企业数据
        if (!StringUtil.isBlank(infoModel.getMerchantName())) {
            long c = dataCounter.incrementAndGet();
            if (this.counterRemindCallback != null) {
                // 更新拉取数量
                this.counterRemindCallback.handleCounter(c);
            }
            if (null != dataTable) {
                Platform.runLater(() -> {
                    dataTable.getItems().add(infoModel);
                    if (dataTable.getItems().size() > 100) {
                        dataTable.getItems().remove(0);
                    }
                });
            }
            dataList.add(infoModel);
            if (dataList.size() > 100) {
                cacheService.saveCrawlerData(this.crawlerReq.getUrl(), dataList);
                dataList.clear();
            }
        }

    }

    private void getClassLabel (Document doc, MerchantInfoModel infoModel, String classId, BiConsumer<MerchantInfoModel, String> consumer) {
        List<String> tagClassList = Arrays.stream(classId.split("//")).collect(Collectors.toList());
        Element currEle = doc.getElementsByTag("html").get(0);
        for (String s : tagClassList) {
            if (currEle == null) return;
            String[] params = s.split("/");
            if (params[0].equals("html")) {
                continue;
            }
            Elements children = currEle.children();
            int ind = Integer.parseInt(params[2]);
            if (children.size() > ind) {
                currEle = children.get(ind);
            } else {
                return;
            }
            // 优先判断对应的class或者ID
//            if (!StringUtil.isBlank(params[1])) {
//                Elements children = currEle.children();
//                for (Element child : children) {
//                    if (params[1].equals(child.className())) {
//                        currEle = child;
//                        break;
//                    } else if (params[1].equals(child.id())) {
//                        currEle = child;
//                        break;
//                    }
//                }
//            } else {
//                Elements children = currEle.children();
//                int ind = Integer.parseInt(params[2]);
//                if (children.size() > ind) {
//                    currEle = children.get(ind);
//                } else {
//                    return;
//                }
//            }
//            if (!StringUtil.isBlank(params[1])) {
//                log.error("DATA ERROR");
//                return;
//            }
        }
        if (null != currEle) {
            consumer.accept(infoModel, currEle.text());
        }
    }

    private void getClassLabel (Document doc, MerchantInfoModel infoModel, String classId, BiConsumer<MerchantInfoModel, String> consumer, ValueType valueType, String domain) {
        List<String> tagClassList = Arrays.stream(classId.split("//")).collect(Collectors.toList());
        Element currEle = doc.firstElementChild();
        for (String s : tagClassList) {
            if (currEle == null) return;
            String[] params = s.split("/");
            if (params[0].equals("html")) {
                continue;
            }
            Elements children = currEle.children();
            int ind = Integer.parseInt(params[2]);
            if (children.size() > ind) {
                currEle = children.get(ind);
            } else {
                return;
            }
//            if (!StringUtil.isBlank(params[1])) {
//                Elements children = currEle.children();
//                for (Element child : children) {
//                    if (params[1].equals(child.className())) {
//                        currEle = child;
//                        break;
//                    } else if (params[1].equals(child.id())) {
//                        currEle = child;
//                        break;
//                    }
//                }
//            } else {
//                Elements children = currEle.children();
//                int ind = Integer.parseInt(params[2]);
//                if (children.size() > ind) {
//                    currEle = children.get(ind);
//                } else {
//                    return;
//                }
//            }
        }
        if (null != currEle) {
            String href = "";
            if (valueType == ValueType.LINK) {
                href = currEle.attr("href");
            } else if (valueType == ValueType.IMAGE) {
                href = currEle.attr("src");
            }
            href = optimizeUrl(domain, href);
            consumer.accept(infoModel, href);
        }
    }
}
