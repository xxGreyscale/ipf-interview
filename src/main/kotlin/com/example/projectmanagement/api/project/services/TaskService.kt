package com.example.projectmanagement.api.project.services

import com.example.projectmanagement.api.project.domain.ProjectTask
import com.example.projectmanagement.api.project.domain.enumerations.TaskStatus
import com.example.projectmanagement.api.project.domain.exceptions.NotAllowedToUpdateStatusException
import com.example.projectmanagement.api.project.domain.exceptions.TaskNotFoundException
import com.example.projectmanagement.api.project.domain.primitives.TaskDescription
import com.example.projectmanagement.api.project.domain.primitives.TaskId
import com.example.projectmanagement.api.project.domain.primitives.TaskName
import com.example.projectmanagement.api.project.io.TaskUpdateRequest
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.example.projectmanagement.common.domain.primitives.UserId
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.time.Instant

@Service
class TaskService(
    private val taskStorageClient: TaskStorageClient,
) {
    fun updateTaskWithId(
        taskId: TaskId,
        taskRequest: TaskUpdateRequest,
        roles: List<UserRole>,
        userId: UserId // Granular access
    ): ProjectTask {
        try {
            val taskNProjectEntity = taskStorageClient.getTaskAndProjectEntityWithTaskId(taskId)
            log.info(taskNProjectEntity)
            val task = taskNProjectEntity.task
            log.info("Task updated: $task")
            if (task.assigneeId != userId && roles.contains(UserRole.ROLE_USER))
                throw NotAllowedToUpdateStatusException("User not allowed to update task")
            if (taskRequest.name != null) task.name = TaskName(taskRequest.name)
            if (taskRequest.description != null) task.description = TaskDescription(taskRequest.description)
            if (taskRequest.assigneeId != null) task.assigneeId = UserId(taskRequest.assigneeId)
            if (taskRequest.status != null) task.status = taskRequest.status
            task.updatedAt = Instant.now()
            return taskStorageClient.updateTask(task, taskNProjectEntity.projectEntity)
        } catch (e: NotAllowedToUpdateStatusException) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(403), e.message.toString())
        } catch (e: TaskNotFoundException) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), "Task with id: ${taskId.asValue()} not found")
        } catch (e: Exception) {
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }
}