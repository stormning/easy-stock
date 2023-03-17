package com.slyak.es.service.impl;

import com.slyak.es.domain.Task;
import org.springframework.data.domain.Persistable;

public interface TaskCompletionHandler<T extends Persistable<Long>> {

    void handleCompletion(Task<T> task);
}
