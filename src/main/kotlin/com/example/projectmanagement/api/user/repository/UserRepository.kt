package com.example.projectmanagement.api.user.repository

import com.example.projectmanagement.api.user.repository.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<UserEntity, String> {
    // custom queries not available within JpaRepository
    fun findByEmail(email: String): UserEntity
}