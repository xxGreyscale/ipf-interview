package com.example.projectmanagement.api.user.services

import com.example.projectmanagement.api.user.domain.User
import com.example.projectmanagement.api.user.repository.UserRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.stereotype.Component

@Component
class UserStorageClient (
    private val userRepository: UserRepository,
    private val userEntityMapper: UserEntityMapper
) {

    fun createUser(user: User): User{
        // Save user in DB
        try {
            val userEntity = userEntityMapper.fromDomain(user)
            val savedEmployee = userRepository.save(userEntity)
            return userEntityMapper.fromEntity(savedEmployee)
        } catch (e: Exception) {
            // throw Exception("Failed to create user")
            // perhaps a custom run exception
            throw Exception("Failed to create user")
        }
    }

    fun getUser(userId: String): User {
        // Get user from DB
        try {
            log.info("Getting user $userId")
            val userEntity = userRepository.findById(userId).get()
            log.info("User found")
            return userEntityMapper.fromEntity(userEntity)
        } catch (e: Exception) {
            throw Exception("Failed to get user", e)
        }
    }

    fun getUserWithEmail(email: String): User {
        try {
            log.info("Getting user with email $email")
            val userEntity = userRepository.findByEmail(email)
            log.info("User found")
            return userEntityMapper.fromEntity(userEntity)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}