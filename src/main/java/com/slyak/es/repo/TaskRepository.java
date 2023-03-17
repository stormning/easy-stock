package com.slyak.es.repo;

import com.slyak.es.domain.Task;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository<T extends Persistable<Long>> extends JpaRepository<Task<T>, Long> {

}