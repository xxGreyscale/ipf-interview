package com.example.projectmanagement.api.authentication.services

import com.example.projectmanagement.common.domain.primitives.UserId
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
class JwtService (
    @Value("\${security.jwt.secret-key}") private val secret: String
) {

    // Generate token with given user name
    fun generateToken(userName: String): String {
        val claims: MutableMap<String, Any> = HashMap()
        return createToken(claims, userName)
    }

    // Create a JWT token with specified claims and subject (username)
    private fun createToken(claims: MutableMap<String, Any>, userName: String): String {
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userName)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token valid for 1 hour (adjust as needed)
            .signWith(getSignKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    // Get the signing key for JWT token
    private fun getSignKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    // Extract the username from the token
    fun extractUsername(token: String?): String {
        return extractClaim(token) { claims -> claims?.subject as String }
    }

    // Extract the expiration date from the token
    private fun extractExpiration(token: String?): Date {
        return extractClaim(token) { claims -> claims?.expiration as Date }
    }

    // Extract a claim from the token
    private fun <T> extractClaim(token: String?, claimsResolver: (Claims?) -> T): T {
        val claims = extractAllClaims(token!!)
        return claimsResolver(claims)
    }

    // Extract all claims from the token
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSignKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    // Check if the token is expired
    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    // Validate the token against user details and expiration
    fun validateToken(token: String?, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        log.info("Got username: $username")
        log.info("Token expired: ${isTokenExpired(token)}   Username: $username == ${userDetails.username}")
        return username == userDetails.username && !isTokenExpired(token)
    }
}