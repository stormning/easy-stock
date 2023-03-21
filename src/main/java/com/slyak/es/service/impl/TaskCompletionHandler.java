package com.slyak.es.service.impl;

import com.slyak.es.domain.Task;

public interface TaskCompletionHandler {

    void handleCompletion(Task task);
}
