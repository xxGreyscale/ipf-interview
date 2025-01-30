package com.example.projectmanagement.api.project.services

import com.example.projectmanagement.api.project.domain.Project
import com.example.projectmanagement.api.project.domain.ProjectTask
import com.example.projectmanagement.api.project.domain.enumerations.TaskStatus
import com.example.projectmanagement.api.project.domain.exceptions.ProjectNotFoundException
import com.example.projectmanagement.api.project.domain.exceptions.TaskNotFoundException
import com.example.projectmanagement.api.project.domain.primitives.ProjectChangeHistory
import com.example.projectmanagement.api.project.domain.primitives.ProjectDescription
import com.example.projectmanagement.api.project.domain.primitives.ProjectId
import com.example.projectmanagement.api.project.domain.primitives.ProjectTitle
import com.example.projectmanagement.api.project.io.ProjectRequest
import com.example.projectmanagement.api.project.io.TaskCreationRequest
import com.example.projectmanagement.api.project.io.UpdateProjectRequest
import com.example.projectmanagement.common.domain.enumarations.ProjectChangeType
import com.example.projectmanagement.common.domain.primitives.UserId
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import java.time.Instant

@Component
class ProjectService (
    private val projectStorageClient: ProjectStorageClient
) {
    fun createProject(
        projectRequest: ProjectRequest,
        managerId: UserId
    ): Project {
        try {
            val project = Project.create(
                managerId = managerId,
                title = projectRequest.name,
                description = projectRequest.description,
                startDate = projectRequest.startDate,
                endDate = projectRequest.endDate
            )
            return projectStorageClient.save(project)
        } catch (e: Exception) {
            log.error("Error creating project: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

    fun getProjects(): List<Project> {
        try {
            return projectStorageClient.projects()
        }
        catch (e: ProjectNotFoundException) {
            log.error("Error fetching projects: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        }
        catch (e: Exception) {
            log.error("Error fetching projects: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

    fun getProjectWithId(id: ProjectId): Project {
        try {
            return projectStorageClient.getProjectWithId(id)
        } catch (e: ProjectNotFoundException) {
            log.error("Error fetching project with id: $id: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: Exception) {
            log.error("Error fetching project with id: $id: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

    fun updateProject(
        projectId: ProjectId,
        projectRequest: UpdateProjectRequest,
    ): Project {
        try {
            // get the project
            val project = projectStorageClient.getProjectWithId(projectId)
            if (projectRequest.name != null) project.title = ProjectTitle(projectRequest.name)
            if (projectRequest.description != null) project.description = ProjectDescription(projectRequest.description)
            if (projectRequest.startDate != null) project.startDate = projectRequest.startDate
            if (projectRequest.endDate != null) project.endDate = projectRequest.endDate
            project.updatedAt = Instant.now()
            val changeHistory = project.changeHistory.toMutableList()
            val change = ProjectChangeHistory(
                change = ProjectChangeType.UPDATED,
                timeStamp = Instant.now()
                // Can add the snapshot here too
            )
            changeHistory.add(change)
            project.changeHistory = changeHistory
            return projectStorageClient.save(project)
        } catch (e: ProjectNotFoundException) {
            log.error("Error updating project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: Exception) {
            log.error("Error updating project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

    fun deleteProject(
        projectId: ProjectId
    ): Boolean {
        try {
            log.info("Deleting project with id: $projectId...")
            return projectStorageClient.deleteProject(projectId)
        } catch (e: ProjectNotFoundException) {
            log.error("Error deleting project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: Exception) {
            log.error("Error deleting project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

    fun createTask(
        taskRequest: TaskCreationRequest,
        projectId: ProjectId
    ): Project {
        try{
            val projectTask = ProjectTask.create(
                name = taskRequest.name,
                assigneeId = UserId(taskRequest.assigneeId),
                description = taskRequest.description,
                projectId = projectId,
                status = TaskStatus.PENDING
            )
            return projectStorageClient.saveTask(projectTask, projectId)
        } catch (e: ProjectNotFoundException) {
            log.error("Error creating task: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: TaskNotFoundException) {
            log.error("Error creating task: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: Exception) {
            log.error("Error creating task: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

    fun getAllTasksByProjectId(
        projectId: ProjectId
    ): List<ProjectTask> {
        try {
            return projectStorageClient.getTasksWithProjectId(projectId)
        } catch (e: ProjectNotFoundException){
            log.error("Error fetching tasks for project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: TaskNotFoundException){
            log.error("Error fetching tasks for project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(404), e.message.toString())
        } catch (e: Exception) {
            log.error("Error fetching tasks for project with id: $projectId: ${e.message}")
            throw HttpClientErrorException(HttpStatusCode.valueOf(500), e.message.toString())
        }
    }

}