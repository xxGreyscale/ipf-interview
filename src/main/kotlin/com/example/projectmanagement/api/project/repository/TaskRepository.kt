package com.example.projectmanagement.api.project.repository

import com.example.projectmanagement.api.project.repository.entities.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository: JpaRepository<TaskEntity, String>