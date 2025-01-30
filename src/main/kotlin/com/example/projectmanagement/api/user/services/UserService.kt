package com.example.projectmanagement.api.user.services

import com.example.projectmanagement.api.user.domain.User
import com.example.projectmanagement.api.user.domain.UserInfoDetails
import com.example.projectmanagement.api.user.io.UserSignupRequest
import com.example.projectmanagement.common.domain.enumarations.UserRole
import com.example.projectmanagement.common.domain.primitives.Email
import com.example.projectmanagement.common.domain.primitives.UserName
import com.example.projectmanagement.common.domain.primitives.UserPassword
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userStorageClient: UserStorageClient,
    private val passwordEncoder: PasswordEncoder
): UserDetailsService {
    fun createUser(
        userRequest: UserSignupRequest
    ): User {
        // Create the user domain
        val user = User.create(
            email = Email(userRequest.email),
            name = UserName(userRequest.name),
            password = UserPassword(passwordEncoder.encode(userRequest.password)),
            userRoles = userRequest.roles.map { UserRole.valueOf(it) }
        )
        return userStorageClient.createUser(user)
    }

    fun getUserById(userId: String): User {
        // Get user from storage
        return userStorageClient.getUser(userId)
    }

    /**
     * @description: Have to function name below, in this case we use email as username
     * */
    override fun loadUserByUsername(email: String): UserDetails {
        return UserInfoDetails(userStorageClient.getUserWithEmail(email))
    }
}