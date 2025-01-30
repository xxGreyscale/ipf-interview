package com.example.projectmanagement.api.project.io

import com.example.projectmanagement.api.project.domain.primitives.ProjectId
import com.example.projectmanagement.api.project.io.mapper.*
import com.example.projectmanagement.api.project.services.ProjectService
import com.example.projectmanagement.api.user.domain.UserInfoDetails
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.example.projectmanagement.common.domain.primitives.UserId
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestController
@RequestMapping("/api/v1/projects")
class ProjectsApi (
    private val projectService: ProjectService,
) {

    @PostMapping("")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    fun createProject(
        @RequestBody request: ProjectRequest,
        authentication: Authentication
    ): ResponseEntity<ProjectResponse> {
        // Get the id of the authenticated user
        return try {
            val userDetails = authentication.principal as UserInfoDetails
            val userId = userDetails.getUserId()
            val response = projectService.createProject(request, UserId(userId))
            ResponseEntity.ok(ProjectApiResponseMapper().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }

    @GetMapping("")
    fun viewProjects(): ResponseEntity< List<ProjectResponse>> {
        return try {
            val response = projectService.getProjects()
            ResponseEntity.ok(ListAllProjectsApiResponse().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }

    }

    @GetMapping("/{id}")
    fun viewProject(
        @PathVariable id: String
    ): ResponseEntity<ProjectResponse> {
        return try {
            val response = projectService.getProjectWithId(ProjectId(id))
            return ResponseEntity.ok(ProjectApiResponseMapper().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    fun updateProject(
        @PathVariable id: String,
        @RequestBody request: UpdateProjectRequest
    ): ResponseEntity<ProjectResponse> {
        return try {
            val response = projectService.updateProject(ProjectId(id), request)
            ResponseEntity.ok(ProjectApiResponseMapper().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MANAGER')")
    fun deleteProject(
        @PathVariable id: String
    ): ResponseEntity<Boolean> {
        return try {
            ResponseEntity.ok(projectService.deleteProject(ProjectId(id)))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }

    @PostMapping("/{projectId}/tasks")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    fun createTask(
        @PathVariable projectId: String,
        @RequestBody taskRequest: TaskCreationRequest
    ): ResponseEntity<ProjectResponse> {
        return try {
            val response = projectService.createTask(taskRequest, ProjectId(projectId))
            ResponseEntity.ok(ProjectApiResponseMapper().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }

    @GetMapping("/{projectId}/tasks")
    fun viewProjectTasks(
        @PathVariable projectId: String
    ): ResponseEntity<List<ProjectTaskInResponse>> {
        return try {
            val response = projectService.getAllTasksByProjectId(ProjectId(projectId))
            ResponseEntity.ok(ListAllTasksApiResponse().toResponse(response))
        } catch (ex: HttpClientErrorException) {
            throw ResponseStatusException(ex.statusCode, ex.message, ex)
        }
    }
}

data class UpdateProjectRequest (
    val name: String?,
    val description: String?,
    val startDate: Instant?,
    val endDate: Instant?,
)

data class ProjectRequest(
    val name: String,
    val description: String,
    val startDate: Instant,
    val endDate: Instant,
)

data class TaskCreationRequest(
    val name: String,
    val description: String,
    val assigneeId: String,
)