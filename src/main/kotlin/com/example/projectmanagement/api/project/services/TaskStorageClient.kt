package com.example.projectmanagement.api.project.services

import com.example.projectmanagement.api.project.domain.ProjectTask
import com.example.projectmanagement.api.project.domain.exceptions.TaskNotFoundException
import com.example.projectmanagement.api.project.domain.primitives.TaskId
import com.example.projectmanagement.api.project.repository.TaskRepository
import com.example.projectmanagement.api.project.repository.entities.ProjectEntity
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Component

@Component
class TaskStorageClient(
    private val taskRepository: TaskRepository,
    private val projectMapper: BaseProjectMapper,
    private val taskMapper: TaskMapper
)
{
    fun updateTask(projectTask: ProjectTask, project: ProjectEntity): ProjectTask {
        try {
            val taskEntity = taskMapper.fromDomain(projectTask, projectMapper.fromEntity(project))
            val savedTask = taskRepository.save(taskEntity)
            return taskMapper.fromEntity(savedTask)
        } catch (e: Exception) {
            throw Exception("Failed to save task")
        }
    }

    fun getTaskAndProjectEntityWithTaskId(taskId: TaskId): TaskWithProjectEntity {
        val task = taskRepository.findById(taskId.asValue())
        if (task.isEmpty) throw TaskNotFoundException("Task with id: ${taskId.asValue()} not found")
        log.info("Task found: ${task.get()}")
        return TaskWithProjectEntity(
            task = taskMapper.fromEntity(task.get()),
            projectEntity = task.get().project
        )
    }


}

data class TaskWithProjectEntity (
    val task: ProjectTask,
    val projectEntity: ProjectEntity
)