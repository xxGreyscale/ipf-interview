package com.example.projectmanagement.common.domain.primitives

import com.example.projectmanagement.common.domain.enumarations.EmailExistsResolution
import com.example.projectmanagement.common.domain.enumarations.UserChangeType
import com.example.projectmanagement.common.domain.exceptions.EmailRuleViolation
import org.apache.commons.lang3.Validate
import java.time.Instant

class Email(email: String) {
    private val value: String

    init {
        try {
            NonBlankString(email)
            Validate.inclusiveBetween(6, 320, email.length)
            Validate.isTrue(email.contains("@") && email.indexOf("@") > 0)
            this.value = email
        } catch (ex: Exception) {
            throw EmailRuleViolation(ex.message ?: "no error description provided", EmailExistsResolution.FAIL_DATA_ERROR_MALFORMED)
        }
    }

    fun asValue(): String {
        return value
    }
}

class UserName(name: String) {
    private val value: String

    init {
        NonBlankString(name)
        Validate.inclusiveBetween(1, 1058, name.length)
        value = name.trim()
    }

    fun asValue(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserName

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class UserPassword(
    password: String,
) {
    private val value: String
    init {
        // We can set up password rules to follow here
        NonBlankString(password)
        Validate.inclusiveBetween(1, 1058, password.length)
        value = password.trim()
    }

    fun asValue(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserPassword

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class UserChangeHistory(
    change: UserChangeType,
    timeStamp: Instant
) {
    private val value: UserChangeType
    private val timestampValue: Instant

    init {
        this.value = change
        this.timestampValue = timeStamp
    }

    fun asValue(): UserChangeType {
        return value
    }

    fun asTimestamp(): Instant {
        return timestampValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserChangeHistory

        if (value != other.value) return false
        if (timestampValue != other.timestampValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + timestampValue.hashCode()
        return result
    }
}