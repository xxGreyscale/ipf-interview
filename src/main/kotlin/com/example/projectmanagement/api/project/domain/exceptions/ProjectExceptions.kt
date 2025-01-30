package com.example.projectmanagement.api.project.domain.exceptions

class ProjectNotFoundException(message: String): RuntimeException(message)
class ProjectAlreadyExistsException(message: String): RuntimeException(message)
class ProjectNotCreatedException(message: String): RuntimeException(message)
class ProjectNotUpdatedException(message: String): RuntimeException(message)
class ProjectNotDeletedException(message: String): RuntimeException(message)
