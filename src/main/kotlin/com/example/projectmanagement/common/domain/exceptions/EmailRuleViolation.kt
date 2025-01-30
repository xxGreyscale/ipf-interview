package com.example.projectmanagement.common.domain.exceptions

import com.example.projectmanagement.common.domain.enumarations.EmailExistsResolution

class EmailRuleViolation(message: String, val errorType: EmailExistsResolution) : DomainPrimitiveRuleViolation(message)