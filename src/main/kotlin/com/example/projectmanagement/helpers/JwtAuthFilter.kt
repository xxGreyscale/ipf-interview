package com.example.projectmanagement.helpers

import com.example.projectmanagement.api.authentication.services.JwtService
import com.example.projectmanagement.api.user.services.UserService
import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
    private val userDetailService: UserService
): OncePerRequestFilter()  {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Retrieve the Authorization header
        val authHeader = request.getHeader("Authorization")
        var token: String? = null
        var username: String? = null

        // Check if the header starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7) // Extract token
            username = jwtService.extractUsername(token) // Extract username from token
        }

        // If the token is valid and no authentication is set in the context
        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            log.info("Setting security context for user: $username")
            val userDetails: UserDetails = userDetailService.loadUserByUsername(username)

            // Validate token and set authentication
            if (jwtService.validateToken(token, userDetails)) {
                log.info("Setting security context for user: $username")
                val authToken = UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response)
    }
}