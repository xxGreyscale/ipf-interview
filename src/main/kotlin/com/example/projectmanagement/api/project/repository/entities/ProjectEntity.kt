package com.example.projectmanagement.api.project.repository.entities

import com.example.projectmanagement.common.domain.enumarations.ProjectChangeType
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "projects")
data class ProjectEntity (
    @Id
    val projectId: String,
    val managerId: String, // Use UserEntity type for the manager
    val title: String,
    val description: String,
    @OneToMany(mappedBy = "project", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val tasks: List<TaskEntity> = emptyList(),
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val startDate: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val endDate: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val updatedAt: Instant,
    @OneToMany(mappedBy = "project",cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val changeHistory: List<ProjectChangeHistoryEntity> = emptyList(),
)

@Entity
data class ProjectChangeHistoryEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Enumerated(EnumType.STRING)
    val change: ProjectChangeType,

    @ManyToOne  // Add the ManyToOne relationship
    @JoinColumn(name = "project_id") // Important: Specify the foreign key column
    val project: ProjectEntity,

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val timeStamp: Instant
    // We can include snapshots, to have the users information at every point in time after a change
    // For this project will end here
)