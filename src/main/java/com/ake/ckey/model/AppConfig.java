package com.ake.ckey.model;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/23 10:52
 */
public interface AppConfig {

    Map<String, Future<?>> globalTaskFutureMap = new HashMap<>();
}
