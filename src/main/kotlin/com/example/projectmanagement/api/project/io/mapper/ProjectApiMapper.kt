package com.example.projectmanagement.api.project.io.mapper

import com.example.projectmanagement.api.project.domain.Project
import com.example.projectmanagement.api.project.domain.ProjectTask
import org.hibernate.query.sqm.tree.SqmNode.log
import java.time.Instant

class ProjectApiResponseMapper {
    fun toResponse(project: Project): ProjectResponse {
        // highly customizable and granular
        return ProjectResponse(
            id = project.projectId.asValue(),
            title = project.title.asValue(),
            description = project.description.asValue(),
            startDate = project.startDate.toString(),
            endDate = project.endDate.toString(),
            tasks = project.projectTasks.map {
                ProjectTaskInResponse(
                    id = it.taskId.asValue(),
                    name = it.name.asValue(),
                    description = it.description.asValue(),
                    assigneeId = it.assigneeId.asValue(),
                    status = it.status.toString(),
                    createdAt = it.createdAt.toString(),
                    updatedAt = it.updatedAt.toString()
                )
            }
        )
    }
}

class ListAllProjectsApiResponse {
    fun toResponse(projects: List<Project>): List<ProjectResponse> {
        return projects.map { it ->
            ProjectResponse(
                id = it.projectId.asValue(),
                title = it.title.asValue(),
                description = it.description.asValue(),
                tasks = it.projectTasks.map {
                    ProjectTaskInResponse(
                        id = it.taskId.asValue(),
                        name = it.name.asValue(),
                        description = it.description.asValue(),
                        assigneeId = it.assigneeId.asValue(),
                        status = it.status.toString(),
                        createdAt = it.createdAt.toString(),
                        updatedAt = it.updatedAt.toString()
                    )
                },
                startDate = it.startDate.toString(),
                endDate = it.endDate.toString()
            )
        }
    }
}

class ListAllTasksApiResponse{
    fun toResponse(projectTasks: List<ProjectTask>): List<ProjectTaskInResponse> {
        return projectTasks.map {
            ProjectTaskInResponse(
                id = it.taskId.asValue(),
                name = it.name.asValue(),
                description = it.description.asValue(),
                assigneeId = it.assigneeId.asValue(),
                status = it.status.toString(),
                createdAt = it.createdAt.toString(),
                updatedAt = it.updatedAt.toString()
            )
        }
    }
}

class TaskApiMapperResponse {
    fun toResponse(projectTask: ProjectTask): ProjectTaskInResponse {
        return ProjectTaskInResponse(
            id = projectTask.taskId.asValue(),
            name = projectTask.name.asValue(),
            description = projectTask.description.asValue(),
            assigneeId = projectTask.assigneeId.asValue(),
            status = projectTask.status.toString(),
            createdAt = projectTask.createdAt.toString(),
            updatedAt = projectTask.updatedAt.toString()
        )
    }
}

class ProjectResponse(
    val id: String,
    val title: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val tasks: List<ProjectTaskInResponse>
)

class ProjectTaskInResponse (
    val id: String,
    val name: String,
    val description: String,
    val assigneeId: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)