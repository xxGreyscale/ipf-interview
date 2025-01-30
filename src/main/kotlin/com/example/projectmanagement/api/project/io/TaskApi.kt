package com.example.projectmanagement.api.project.io

import com.example.projectmanagement.api.project.domain.enumerations.TaskStatus
import com.example.projectmanagement.api.project.domain.primitives.TaskId
import com.example.projectmanagement.api.project.io.mapper.ProjectTaskInResponse
import com.example.projectmanagement.api.project.io.mapper.TaskApiMapperResponse
import com.example.projectmanagement.api.project.services.TaskService
import com.example.projectmanagement.api.user.domain.UserInfoDetails
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.example.projectmanagement.common.domain.primitives.UserId
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/tasks")
class TaskApi(
    private val taskService: TaskService
)
{
    @PutMapping("/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    fun updateTask(
        @PathVariable taskId: String,
        @RequestBody request: TaskUpdateRequest,
        authentication: Authentication
    ): ResponseEntity<ProjectTaskInResponse> {
        return try {
            val userDetails = authentication.principal as UserInfoDetails
            // if user not admin, dont change assignee
            val userId = UserId(userDetails.getUserId())
            val roles = authentication.authorities.map {
                UserRole.valueOf(it.toString())
            }
            if (roles.contains(UserRole.ROLE_USER) && request.assigneeId != null)
                throw HttpClientErrorException(HttpStatus.FORBIDDEN, "User not allowed to change assignee")
            val response = taskService.updateTaskWithId(TaskId(taskId), request, roles, userId)
            log.info(response)
            ResponseEntity.ok(TaskApiMapperResponse().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            log.info("Failed to update task with error: ${ex.message}")
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }

    @PutMapping("/{taskId}/status")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    fun updateStatus(
        @PathVariable taskId: String,
        @Valid @RequestBody statusUpdateRequest: TaskStatusUpdateRequest,
        authentication: Authentication
    ): ResponseEntity<ProjectTaskInResponse> {
        return try {
            val userDetails = authentication.principal as UserInfoDetails
            val userId = UserId(userDetails.getUserId())
            val roles = authentication.authorities.map {
                UserRole.valueOf(it.toString())
            }
            val request = TaskUpdateRequest(status = statusUpdateRequest.status)
            val response = taskService.updateTaskWithId(TaskId(taskId), request, roles, userId)
            ResponseEntity.ok(TaskApiMapperResponse().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }
}
data class TaskStatusUpdateRequest (
    @field:NotBlank(message = "Title cannot be blank")
    @Enumerated(EnumType.STRING)
    val status: TaskStatus
)

data class TaskUpdateRequest (
    val name: String?= null,
    val description: String?= null,
    val assigneeId: String?= null,
    @Enumerated(EnumType.STRING)
    val status: TaskStatus
)