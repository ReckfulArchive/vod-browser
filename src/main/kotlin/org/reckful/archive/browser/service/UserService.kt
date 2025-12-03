package org.reckful.archive.browser.service

import org.reckful.archive.browser.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String?): UserDetails? {
        if (username.isNullOrBlank()) {
            throw UsernameNotFoundException("There's definitely no user with an empty name")
        }

        val userEntity = userRepository.findByName(username)
            ?: throw UsernameNotFoundException("User $username not found")

        val authorities = userRepository.loadAuthorities(userEntity.id)
            .map { SimpleGrantedAuthority(it) }

        return User(userEntity.name, userEntity.passwordEncoded, authorities)
    }
}
