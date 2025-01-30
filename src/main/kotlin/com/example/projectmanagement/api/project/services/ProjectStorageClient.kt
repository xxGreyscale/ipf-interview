package com.example.projectmanagement.api.project.services

import com.example.projectmanagement.api.project.domain.Project
import com.example.projectmanagement.api.project.domain.ProjectTask
import com.example.projectmanagement.api.project.domain.exceptions.ProjectNotFoundException
import com.example.projectmanagement.api.project.domain.exceptions.TaskCouldNotBeSavedException
import com.example.projectmanagement.api.project.domain.exceptions.TaskNotFoundException
import com.example.projectmanagement.api.project.domain.primitives.ProjectId
import com.example.projectmanagement.api.project.repository.ProjectRepository
import com.example.projectmanagement.api.project.repository.entities.ProjectEntity
import com.example.projectmanagement.api.project.repository.entities.TaskEntity
import org.springframework.stereotype.Component

@Component
class ProjectStorageClient(
    private val projectRepository: ProjectRepository,
    private val projectMapper: BaseProjectMapper,
    private val taskMapper: TaskMapper,
) {
    fun save(project: Project): Project {
        val saveProjectEntity = projectRepository.save(projectMapper.fromDomain(project))
        return projectMapper.fromEntity(saveProjectEntity)
    }

    fun projects(): List<Project> {
        val projects = projectRepository.findAll()
        if(projects.isEmpty()) throw ProjectNotFoundException("No projects found")
        return projects.map { projectMapper.fromEntity(it) }
    }

    fun getProjectWithId(id: ProjectId): Project {
        val project = projectRepository.findById(id.asValue())
        if (project.isEmpty) throw ProjectNotFoundException("Project with id: $id not found")
        return projectMapper.fromEntity(project.get())
    }

    fun deleteProject(id: ProjectId): Boolean {
        val project = projectRepository.findById(id.asValue())
        if (project.isEmpty) throw ProjectNotFoundException("Project with id: $id not found")
        // Proceed to delete after
        projectRepository.deleteById(id.asValue())
        return true
    }

    fun saveTask(projectTask: ProjectTask, projectId: ProjectId): Project {
        val projectEntity = projectRepository.findById(projectId.asValue())
        if (projectEntity.isEmpty) throw ProjectNotFoundException("Project with id: $projectId not found")
        val taskEntity = TaskEntity(
            id = projectTask.taskId.asValue(),
            name = projectTask.name.asValue(),
            assigneeId = projectTask.assigneeId.asValue(),
            description = projectTask.description.asValue(),
            project = projectEntity.get(),
            status = projectTask.status,
            createdAt = projectTask.createdAt,
            updatedAt = projectTask.updatedAt
        )
        val tasks = projectEntity.get().tasks.toMutableList()
        tasks.add(taskEntity)
        val projectToSave = ProjectEntity(
            projectId = projectEntity.get().projectId,
            managerId = projectEntity.get().managerId,
            title = projectEntity.get().title,
            description = projectEntity.get().description,
            tasks = tasks,
            changeHistory = projectEntity.get().changeHistory,
            startDate = projectEntity.get().startDate,
            endDate = projectEntity.get().endDate,
            updatedAt = projectEntity.get().updatedAt
        )
        val updatedProject = projectRepository.save(projectToSave)
        if (updatedProject.tasks.isEmpty()) throw TaskCouldNotBeSavedException("Task could not be saved")
        return projectMapper.fromEntity(updatedProject)
    }

    fun getTasksWithProjectId(projectId: ProjectId): List<ProjectTask> {
        val project = projectRepository.findById(projectId.asValue())
        if (project.isEmpty) throw ProjectNotFoundException("Project with $projectId could not be found")
        if(project.get().tasks.isEmpty()) throw TaskNotFoundException("No tasks found for project with id: $projectId")
        return project.get().tasks.map { taskMapper.fromEntity(it) }
    }
}