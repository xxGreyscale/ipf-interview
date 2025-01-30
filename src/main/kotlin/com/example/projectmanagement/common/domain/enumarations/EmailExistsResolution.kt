package com.example.projectmanagement.common.domain.enumarations

enum class EmailExistsResolution {
    SUCCESS_EXISTS,
    SUCCESS_DOES_NOT_EXIST,
    FAIL_DATA_ERROR_MALFORMED,
    FAIL_DB_ERROR,
}