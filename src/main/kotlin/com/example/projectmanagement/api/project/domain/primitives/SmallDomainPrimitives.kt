package com.example.projectmanagement.api.project.domain.primitives

import com.example.projectmanagement.common.domain.enumarations.ProjectChangeType
import com.example.projectmanagement.common.domain.primitives.NonBlankString
import org.apache.commons.lang3.Validate
import java.time.Instant

class ProjectTitle(
    title: String
) {
    private val value: String
    init {
            NonBlankString(title)
            Validate.inclusiveBetween(1, 1058, title.length)
            value = title.trim()
    }

    fun asValue(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectTitle

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class ProjectDescription(
    description: String
) {
    private val value: String

    init {
        Validate.inclusiveBetween(0, 3000, description.length)
        value = description.trim()
    }

    fun asValue(): String = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectDescription

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class ProjectChangeHistory(
    change: ProjectChangeType,
    timeStamp: Instant
) {
    private val value: ProjectChangeType
    private val timestampValue: Instant

    init {
        this.value = change
        this.timestampValue = timeStamp
    }

    fun asValue(): ProjectChangeType {
        return value
    }

    fun asTimestamp(): Instant {
        return timestampValue
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectChangeHistory

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

class TaskName(
    val name: String
) {
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

        other as TaskName

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}

class TaskDescription(
    val description: String
) {
    private val value: String

    init {
        Validate.inclusiveBetween(0, 3000, description.length)
        value = description.trim()
    }

    fun asValue(): String {
        return value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskDescription

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}