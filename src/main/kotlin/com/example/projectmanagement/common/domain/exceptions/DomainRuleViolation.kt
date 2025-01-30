package com.example.projectmanagement.common.domain.exceptions

open class DomainRuleViolation(msg: String, cause: Throwable? = null) : RuntimeException(msg, cause)
