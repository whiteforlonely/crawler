package com.ake.ckey.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/21 11:09
 */
public enum PageTypeEnum {

    PRE_NEXT("pre-next", "上下页"),
    NUMBER("pageNum", "数字页"),
    ;

    private final String code;
    private final String desc;

    PageTypeEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private static Map<String, PageTypeEnum> map;

    static {
        map = Arrays.stream(values()).collect(Collectors.toMap(PageTypeEnum::getDesc, Function.identity(), (oldVal, newVal) -> newVal));
    }

    public static PageTypeEnum typeOf(String code) {
        return map.get(code);
    }
}
