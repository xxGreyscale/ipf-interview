package com.example.projectmanagement.api.project.domain

import com.example.projectmanagement.api.project.domain.primitives.ProjectChangeHistory
import com.example.projectmanagement.api.project.domain.primitives.ProjectDescription
import com.example.projectmanagement.api.project.domain.primitives.ProjectId
import com.example.projectmanagement.api.project.domain.primitives.ProjectTitle
import com.example.projectmanagement.common.domain.enumarations.ProjectChangeType
import com.example.projectmanagement.common.domain.primitives.UserId
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant


data class Project(
    val projectId: ProjectId,
    val managerId: UserId,
    var title: ProjectTitle,
    var description: ProjectDescription,
    var projectTasks: List<ProjectTask> = emptyList(),
    var changeHistory: List<ProjectChangeHistory> = emptyList(), // can be in metadata
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    var startDate: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    var endDate: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    var updatedAt: Instant
) {
    companion object {
        fun create(
            title: String,
            description: String,
            managerId: UserId,
            startDate: Instant,
            endDate: Instant
        ): Project {
            val now = Instant.now()
            val changeHistory = ProjectChangeHistory(ProjectChangeType.CREATED, now)
            return Project(
                ProjectId.generate(),
                managerId,
                ProjectTitle(title),
                ProjectDescription(description),
                listOf(),
                listOf(changeHistory),
                startDate,
                endDate,
                now
            )
        }
    }
}
