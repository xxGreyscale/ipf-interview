package com.example.projectmanagement.common.domain.exceptions

open class DomainPrimitiveRuleViolation(override val message: String) : DomainRuleViolation(message)
