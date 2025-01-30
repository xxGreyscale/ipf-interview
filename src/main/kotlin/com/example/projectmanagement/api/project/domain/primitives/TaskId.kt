package com.example.projectmanagement.api.project.domain.primitives

import org.apache.commons.lang3.Validate
import java.util.*

private const val ID_PREFIX = "task"
private const val EXPECTED_TASK_ID_LENGTH = 37 + ID_PREFIX.length

class TaskId (
    id: String
) {
    private val value: String

    init {
        // TODO - this should be hardened to specific length and regexp check
        Validate.isTrue(id.isNotBlank())
        Validate.inclusiveBetween(
            EXPECTED_TASK_ID_LENGTH,
            EXPECTED_TASK_ID_LENGTH,
            id.length,
            "Task Id Should be of required length"
        )
        val regex = Regex("^$ID_PREFIX-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        Validate.isTrue(regex.matches(id), "Did not match expected format")
        this.value = id
    }

    fun asValue(): String = value

    override fun toString(): String = "TaskId($value)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TaskId

        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()

    companion object {
        fun generate(): TaskId {
            return TaskId("$ID_PREFIX-${UUID.randomUUID()}")
        }
    }
}