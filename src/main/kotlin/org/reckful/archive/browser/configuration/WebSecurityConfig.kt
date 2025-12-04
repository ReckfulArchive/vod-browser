package org.reckful.archive.browser.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebMvcConfigurer {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(LOGIN_PAGE_URL, permitAll)

                authorize("/playlists", authenticated)
                authorize("/playlist/**", authenticated)
                authorize("/vod/{vodId}/edit", authenticated)

                authorize(anyRequest, permitAll)
            }
            formLogin {
                loginPage = LOGIN_PAGE_URL
                defaultSuccessUrl("/", true)
                permitAll()
            }
            logout {
                logoutUrl = LOGOUT_PAGE_URL
                logoutSuccessUrl = "/"
                permitAll()
            }
        }
        return http.build()
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController(LOGIN_PAGE_URL).setViewName("mod/login")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    private companion object {
        // Prefixed by /mod to discourage scanners and bots from trying to bruteforce / exploit logins.
        // Not much of an obstacle tbh (esp. since this code is open source), but it's better than nothing
        private const val LOGIN_PAGE_URL = "/mod/login"
        private const val LOGOUT_PAGE_URL = "/mod/logout"
    }
}
