package com.diao.taskflowapi.repositories;

import com.diao.taskflowapi.entities.Task;
import com.diao.taskflowapi.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acces aux donnees pour l'entite {@link Task}.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByAssigneeId(Long assigneeId, Pageable pageable);

    Page<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status, Pageable pageable);

    long countByProjectIdAndStatus(Long projectId, TaskStatus status);

    long countByProjectId(Long projectId);
}