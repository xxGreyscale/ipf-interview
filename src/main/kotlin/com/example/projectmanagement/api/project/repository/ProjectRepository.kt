package com.example.projectmanagement.api.project.repository

import com.example.projectmanagement.api.project.repository.entities.ProjectEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepository: JpaRepository<ProjectEntity, String>