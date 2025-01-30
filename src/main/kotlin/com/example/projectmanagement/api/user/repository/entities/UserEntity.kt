package com.example.projectmanagement.api.user.repository.entities

import com.example.projectmanagement.common.domain.enumarations.UserChangeType
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
data class UserEntity (
    @Id
    val userId: String,
    val name: String,
    val email: String,
    val password: String,
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Enumerated(EnumType.STRING)
    val roles: Set<UserRole> = emptySet(),
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val createdAt: Instant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val updatedAt: Instant,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val changeHistory: List<UserChangeHistoryEntity> = emptyList()
)

@Entity
data class UserChangeHistoryEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Enumerated(EnumType.STRING)
    val change: UserChangeType,

    @ManyToOne  // Add the ManyToOne relationship
    @JoinColumn(name = "user_id") // Important: Specify the foreign key column
    val user: UserEntity,

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC") // ISO 8601 format
    val timeStamp: Instant
    // We can include snapshots, to have the users information at every point in time after a change
)