package com.example.projectmanagement.common.domain.primitives

import com.example.projectmanagement.common.domain.exceptions.DomainPrimitiveRuleViolation
import org.apache.commons.lang3.Validate


class NonBlankString(str: String) {
    private val value: String

    init {
        try {
            Validate.isTrue(str.isNotBlank(), "Value should not be blank")
            Validate.inclusiveBetween(1, 3000, str.length, "Should be of valid length")
            this.value = str.trim()
        } catch (ex: IllegalArgumentException) {
            throw DomainPrimitiveRuleViolation(ex.message!!)
        }
    }

    fun asValue(): String = value
}

class BoundedString(str: String, minLength: Int, maxLength: Int) {
    private val value: String

    init {
        try {
            Validate.isTrue(str.isNotBlank(), "Value should not be blank")
            Validate.inclusiveBetween(minLength, maxLength, str.length, "Should be of valid length")
            this.value = str.trim()
        } catch (ex: IllegalArgumentException) {
            throw DomainPrimitiveRuleViolation(ex.message!!)
        }
    }

    fun asValue(): String = value
}