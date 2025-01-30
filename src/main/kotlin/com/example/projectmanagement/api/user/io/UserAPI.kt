package com.example.projectmanagement.api.user.io

import com.example.projectmanagement.api.user.io.mapper.UserSignupApiMapper
import com.example.projectmanagement.api.user.services.UserService
import io.swagger.v3.oas.annotations.Operation
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/users")
class UserAPI (
    val userService: UserService,
) {
    @PostMapping("/signup")
    @Operation(
        summary = "Signup project management user",
        description = "This endpoint Registers project manager user according to input by the user",
    )
    fun signup(
        @RequestBody request: UserSignupRequest,
    ): ResponseEntity<UserSignupResponse> {
        return try {
            val response = userService.createUser(request)
            ResponseEntity.ok(UserSignupApiMapper().toResponse(response))
        } catch (e: Exception) {
            log.info("Error creating user: ${e.message}")
            ResponseEntity.badRequest().build()
        }
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Get user by id",
        description = "This endpoint fetches user by id",
    )
    fun getUserById(
        @PathVariable userId: String,
    ): ResponseEntity<UserSignupResponse> {
        val response = userService.getUserById(userId)
        return ResponseEntity.ok(UserSignupApiMapper().toResponse(response))
    }
}

data class UserSignupRequest(
    val email: String,
    val password: String,
    val name: String,
    val roles: List<String>,
)

data class UserSignupResponse(
    val id: String,
    val email: String,
    val name: String,
    val roles: List<String>,
)