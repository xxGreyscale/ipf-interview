package com.example.projectmanagement.api.user.domain

import com.example.projectmanagement.common.domain.primitives.Email
import com.example.projectmanagement.common.domain.primitives.UserId
import com.example.projectmanagement.common.domain.primitives.UserName
import com.example.projectmanagement.common.domain.primitives.UserPassword
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserInfoDetails(
    private val user: User
): UserDetails {
//    our username for this case is email
    private val userId: UserId = this.user.userId
    private val username: Email = this.user.email
    private val password: UserPassword = this.user.password ?: throw Exception("User has no password")
    private val authorities: List<GrantedAuthority> = user.userRoles.map { SimpleGrantedAuthority(it.name)}

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return authorities
    }

    override fun getPassword(): String {
        return password.asValue()
    }

    override fun getUsername(): String {
        return username.asValue()
    }

    fun getUserId(): String {
        return userId.asValue()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

}