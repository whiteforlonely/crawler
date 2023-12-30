package com.ake.ckey.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/23 10:29
 */
public enum TaskStatusEnum {

    READY       (0, "未开始"),
    RUNNING     (1, "进行中"),
    EXCEPTION   (2, "异常结束"),
    DONE        (3, "已完成"),
    ;
    private final int code;
    private final String desc;

    static Map<Integer, TaskStatusEnum> codeMap = new HashMap<>();

    static {
        for (TaskStatusEnum taskStatusEnum : values()) {
            codeMap.put(taskStatusEnum.getCode(), taskStatusEnum);
        }
    }
    TaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TaskStatusEnum typeOf(int code) {
        return codeMap.get(code);
    }
}
