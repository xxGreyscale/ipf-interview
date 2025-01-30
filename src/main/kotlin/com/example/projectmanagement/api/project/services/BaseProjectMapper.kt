package com.example.projectmanagement.api.project.services

import com.example.projectmanagement.api.project.domain.Project
import com.example.projectmanagement.api.project.domain.ProjectTask
import com.example.projectmanagement.api.project.domain.primitives.*
import com.example.projectmanagement.api.project.repository.entities.ProjectChangeHistoryEntity
import com.example.projectmanagement.api.project.repository.entities.ProjectEntity
import com.example.projectmanagement.api.project.repository.entities.TaskEntity
import com.example.projectmanagement.common.domain.primitives.UserId
import org.springframework.stereotype.Component

@Component
class BaseProjectMapper (
    private val projectChangeHistoryMapper: ProjectChangeHistoryMapper
) {
    fun fromEntity(entity: ProjectEntity): Project {
        // Map entity to domain
        return Project(
            projectId = ProjectId(entity.projectId),
            managerId = UserId(entity.managerId),
            title = ProjectTitle(entity.title),
            description = ProjectDescription(entity.description),
            changeHistory = projectChangeHistoryMapper.fromEntity(entity.changeHistory),
            startDate = entity.startDate,
            endDate = entity.endDate,
            updatedAt = entity.updatedAt,
            projectTasks = entity.tasks.map { ProjectTask(
                taskId = TaskId(it.id),
                name = TaskName(it.name),
                assigneeId = UserId(it.assigneeId),
                description = TaskDescription(it.description),
                projectId = ProjectId(it.project.projectId),
                status = it.status,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            ) }
        )
    }

    fun fromDomain(domain: Project): ProjectEntity {
        // Map domain to entity
        return ProjectEntity(
            projectId = domain.projectId.asValue(),
            managerId = domain.managerId.asValue(),
            title = domain.title.asValue(),
            description = domain.description.asValue(),
            startDate = domain.startDate,
            endDate = domain.endDate,
            updatedAt = domain.updatedAt,
            changeHistory = projectChangeHistoryMapper.fromDomain(domain.changeHistory, domain),
            tasks = domain.projectTasks.map { TaskEntity(
                id = it.taskId.asValue(),
                name = it.name.asValue(),
                assigneeId = it.assigneeId.asValue(),
                description = it.description.asValue(),
                status = it.status,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                project = ProjectEntity(
                    projectId = domain.projectId.asValue(),
                    managerId = domain.managerId.asValue(),
                    title = domain.title.asValue(),
                    description = domain.description.asValue(),
                    startDate = domain.startDate,
                    endDate = domain.endDate,
                    updatedAt = domain.updatedAt,
                    changeHistory = emptyList()
            )) }
        )
    }

}

@Component
class ProjectChangeHistoryMapper {
 fun fromEntity(entity: List<ProjectChangeHistoryEntity>): List<ProjectChangeHistory> {
     // Map entity to domain
     return entity.map {
         ProjectChangeHistory(
             change = it.change,
             timeStamp = it.timeStamp
         )
     }
 }

fun fromDomain(domain: List<ProjectChangeHistory>, project: Project): List<ProjectChangeHistoryEntity> {
     // Map domain to entity
     return domain.map {
         ProjectChangeHistoryEntity(
             id = 0L,
             change = it.asValue(),
                project = ProjectEntity(
                    projectId = project.projectId.asValue(),
                    managerId = project.managerId.asValue(),
                    title = project.title.asValue(),
                    description = project.description.asValue(),
                    startDate = project.startDate,
                    endDate = project.endDate,
                    updatedAt = project.updatedAt,
                    changeHistory = emptyList()
                ),
             timeStamp = it.asTimestamp()
         )
     }
 }
}

@Component
class TaskMapper {
    fun fromEntity(entity: TaskEntity): ProjectTask {
        // Map entity to domain
        return ProjectTask(
            taskId = TaskId(entity.id),
            name = TaskName(entity.name),
            assigneeId = UserId(entity.assigneeId),
            description = TaskDescription(entity.description),
            projectId = ProjectId(entity.project.projectId),
            status = entity.status,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun fromDomain(domain: ProjectTask, project: Project): TaskEntity {
        // Map domain to entity
        return TaskEntity(
            id = domain.taskId.asValue(),
            name = domain.name.asValue(),
            assigneeId = domain.assigneeId.asValue(),
            description = domain.description.asValue(),
            project = ProjectEntity(
                projectId = project.projectId.asValue(),
                managerId = project.managerId.asValue(),
                title = project.title.asValue(),
                description = project.description.asValue(),
                startDate = project.startDate,
                endDate = project.endDate,
                updatedAt = project.updatedAt,
                changeHistory = emptyList()
            ),
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}