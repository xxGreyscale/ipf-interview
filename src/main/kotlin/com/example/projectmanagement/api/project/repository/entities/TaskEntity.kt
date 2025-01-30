package com.example.projectmanagement.api.project.repository.entities

import com.example.projectmanagement.api.project.domain.enumerations.TaskStatus
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.Instant

@Entity
class TaskEntity (
    @Id
    val id: String,
    val name: String,

    @ManyToOne  // Add the ManyToOne relationship
    @JoinColumn(name = "project_id") // Important: Specify the foreign key column
    val project: ProjectEntity,

    val description: String,
    val assigneeId: String,
    @Enumerated(EnumType.STRING)
    val status: TaskStatus,
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val createdAt: Instant,
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val updatedAt: Instant,
    // We can include snapshots, to have the users information at every point in time after a change
    // For this project will end here
)