package com.example.projectmanagement.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class LogoutHandler: LogoutHandler {
    override fun logout(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication?) {
        // Retrieve the Authorization header
        val authHeader = request?.getHeader("Authorization")
        var token: String? = null

        // Check if the header starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        val jwt = authHeader.substring(7)
        // here we can decide to delete it in redis or not
        log.info(SecurityContextHolder.getContext())
        // implement a password invalidation here within our DB or Redis
    }
}