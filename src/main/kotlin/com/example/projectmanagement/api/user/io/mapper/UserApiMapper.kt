package com.example.projectmanagement.api.user.io.mapper

import com.example.projectmanagement.api.user.domain.User
import com.example.projectmanagement.api.user.io.UserSignupResponse
import org.springframework.stereotype.Component

class UserSignupApiMapper () {
    fun toResponse(user: User): UserSignupResponse {
        // highly customizable and granular
        return UserSignupResponse(
            id = user.userId.asValue(),
            email = user.email.asValue(),
            name = user.name.asValue(),
            roles = user.userRoles.map { it.name }
        )
    }
}