package com.example.projectmanagement.api.project.domain.primitives

import org.apache.commons.lang3.Validate
import java.util.*

private const val ID_PREFIX = "pr"
private const val EXPECTED_PROJECT_ID_LENGTH = 37 + ID_PREFIX.length

class ProjectId(
    id: String
) {
    private val id: String

    init {
        // TODO - this should be hardened to specific length and regexp check
        Validate.isTrue(id.isNotBlank())
        Validate.inclusiveBetween(
            EXPECTED_PROJECT_ID_LENGTH,
            EXPECTED_PROJECT_ID_LENGTH,
            id.length,
            "Project Id Should be of required length"
        )
        val regex = Regex("^$ID_PREFIX-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
        Validate.isTrue(regex.matches(id), "Did not match expected format")
        this.id = id
    }

    fun asValue(): String = id

    override fun toString(): String = "ProjectId($id)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProjectId

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun generate(): ProjectId {
            return ProjectId("$ID_PREFIX-${UUID.randomUUID()}")
        }
    }

}