package com.example.projectmanagement.api.user.domain

import com.example.projectmanagement.common.domain.enumarations.UserChangeType
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.example.projectmanagement.common.domain.primitives.*
import java.time.Instant


data class User (
    val userId: UserId,
    val name: UserName,
    val email: Email,
    val password: UserPassword? = null,
    val userRoles: List<UserRole>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val changeHistory: List<UserChangeHistory> = emptyList() // can be in metadata
) {

    companion object{

        fun create(
            name: UserName,
            email: Email,
            password: UserPassword,
            userRoles: List<UserRole>,
        ): User {
            val now = Instant.now()
            val changeHistory = UserChangeHistory(UserChangeType.CREATED, now)
            return User(
                UserId.generate(),
                name,
                email,
                password,
                userRoles,
                now,
                now,
                listOf(changeHistory)
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        return userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }
}