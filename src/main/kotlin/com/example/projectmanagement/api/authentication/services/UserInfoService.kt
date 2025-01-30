package com.example.projectmanagement.api.authentication.services

import com.example.projectmanagement.api.user.services.UserStorageClient

import com.example.projectmanagement.api.user.domain.User
import com.example.projectmanagement.api.user.domain.UserInfoDetails
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserInfoService (
    private val userStorageClient: UserStorageClient,
): UserDetailsService {

    /**
     * @description: Have to function name below, in this case we use email as username
     * */
    override fun loadUserByUsername(email: String): UserDetails {
        return UserInfoDetails(userStorageClient.getUserWithEmail(email))
    }
//    Mostly use or include an external provider in this
}