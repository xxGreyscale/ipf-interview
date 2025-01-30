package com.example.projectmanagement.api.user.services

import com.example.projectmanagement.api.user.domain.User
import com.example.projectmanagement.api.user.repository.entities.UserChangeHistoryEntity
import com.example.projectmanagement.api.user.repository.entities.UserEntity
import com.example.projectmanagement.common.domain.enumarations.UserChangeType
import com.example.projectmanagement.common.domain.primitives.*
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Component

@Component
class UserEntityMapper (
    private val userChangeHistoryMapper: UserChangeHistoryMapper
) {
    fun fromEntity(entity: UserEntity): User {
        // Map entity to domain
        log.info("Mapping entity to domain")
        log.info(entity.userId)
        log.info(entity.email)
        log.info(entity.name)
        log.info(entity.password)
        log.info(entity.roles.toString())
        log.info(entity.createdAt.toString())
        log.info(entity.updatedAt.toString())
        try {
            return User(
                userId = UserId(entity.userId),
                email = Email(entity.email),
                name = UserName(entity.name),
                userRoles = entity.roles.map { it  },
                password = UserPassword(entity.password),
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                changeHistory = userChangeHistoryMapper.fromEntity(entity.changeHistory)
            )
        } catch (e: Exception) {
            throw Exception("Failed to map entity to domain")
        }
    }

    fun fromDomain(domain: User): UserEntity {
        // Map domain to entity
        return UserEntity(
            userId = domain.userId.asValue(),
            email = domain.email.asValue(),
            name = domain.name.asValue(),
            password = domain.password!!.asValue(),
            roles = domain.userRoles.toSet(),
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            changeHistory = domain.changeHistory.map { userChangeHistoryMapper.fromDomain(it, domain) }
        )
    }
}

@Component
class UserChangeHistoryMapper {
    fun fromEntity(entity: List<UserChangeHistoryEntity>): List<UserChangeHistory> {
        // Map entity to domain
        return entity.map {
            UserChangeHistory(
            change = it.change,
            timeStamp = it.timeStamp
        ) }
    }

    fun fromDomain(domain: UserChangeHistory, user: User): UserChangeHistoryEntity {
        // Map domain to entity
        return UserChangeHistoryEntity(
            id = 0L,
            user = UserEntity(
                userId = user.userId.asValue(),
                email = user.email.asValue(),
                name = user.name.asValue(),
                password = user.password!!.asValue(),
                roles = user.userRoles.toSet(),
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                changeHistory = emptyList()
            ),
            change = domain.asValue(),
            timeStamp = domain.asTimestamp()
        )
    }
}