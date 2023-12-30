package com.ake.ckey.callback;

import com.ake.ckey.enums.TaskStatusEnum;

/**
 * @author saturday
 * @version 1.0.0
 * date: 2023/12/23 10:37
 */
public interface TaskStatusCallback {

    void handleTaskStatus(TaskStatusEnum taskStatusEnum);
}
