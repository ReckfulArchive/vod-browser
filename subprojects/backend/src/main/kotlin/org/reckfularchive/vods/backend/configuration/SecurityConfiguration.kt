package org.reckfularchive.vods.backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize("/actuator/**", hasRole("actuator"))
                authorize(anyRequest, permitAll)
            }
        }
        return http.build()
    }

    @Bean
    fun users(): UserDetailsService {
        val admin = User.builder()
            .username("admin")
            .password("{noop}admin")
            .roles("actuator")
            .build()

        return InMemoryUserDetailsManager(admin)
    }
}
