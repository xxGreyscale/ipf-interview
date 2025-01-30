package com.example.projectmanagement.api.project.domain.exceptions

class TaskNotFoundException(message: String): RuntimeException(message)
class TaskCouldNotBeSavedException(message: String): RuntimeException(message)
class NotAllowedToUpdateStatusException(message: String): RuntimeException(message)