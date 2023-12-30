package com.ake.ckey.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/22 12:13
 */
public class BaseService {

    protected String getDomain(String url) {
        Pattern pattern = Pattern.compile("http[s]?://[^/]+");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group() + "/";
        }
        return "";
    }

    protected String leftPad(String a, int length, char padding) {
        if (a.length() >= length) return a;
        int paddingLength = length - a.length();
        for (int i = 0; i < paddingLength; i++) {
            a = padding + a;
        }
        return a;
    }
}
