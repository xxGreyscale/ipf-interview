package com.example.projectmanagement.api.authentication.io

import com.example.projectmanagement.api.authentication.services.JwtService
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationApi(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    @PostMapping("/generate-token")
    fun generateToken(
        @RequestBody request: AuthenticationRequest
    ): ResponseEntity<String> {
        return try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )
            if (authentication.isAuthenticated) {
                ResponseEntity(jwtService.generateToken(request.email), HttpStatus.OK)
            } else {
                ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
            }
        } catch (e: Exception) {
            log.info("Error generating token: ${e.message}")
            ResponseEntity("Unauthorized", HttpStatus.UNAUTHORIZED)
        }
    }
}

data class AuthenticationRequest(
    val email: String, // for the principal, email is used as username
    val password: String,
)