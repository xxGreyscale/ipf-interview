package com.example.projectmanagement.api.project.domain

import com.example.projectmanagement.api.project.domain.enumerations.TaskStatus
import com.example.projectmanagement.api.project.domain.primitives.ProjectId
import com.example.projectmanagement.api.project.domain.primitives.TaskDescription
import com.example.projectmanagement.api.project.domain.primitives.TaskId
import com.example.projectmanagement.api.project.domain.primitives.TaskName
import com.example.projectmanagement.common.domain.primitives.UserId
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

data class ProjectTask(
    val taskId: TaskId,
    var name: TaskName,
    var description: TaskDescription,
    val projectId: ProjectId,
    var assigneeId: UserId,
    var status: TaskStatus,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    val createdAt: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    var updatedAt: Instant,
) {
    companion object {
        fun create(
            name: String,
            description: String,
            projectId: ProjectId,
            assigneeId: UserId,
            status: TaskStatus,
        ): ProjectTask {
            val now = Instant.now()
            // We can validate to make sure that the projectId and userId are correct
            return ProjectTask(
                TaskId.generate(),
                TaskName(name),
                TaskDescription(description),
                projectId,
                assigneeId,
                status,
                now,
                now
            )
        }
    }
}