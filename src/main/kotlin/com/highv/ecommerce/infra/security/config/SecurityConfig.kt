package com.highv.ecommerce.infra.security.config

import com.highv.ecommerce.infra.security.CustomAuthenticationEntryPoint
import com.highv.ecommerce.infra.security.jwt.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationEntrypoint: AuthenticationEntryPoint,
    private val accessDeniedHandler: AccessDeniedHandler,
) {
    @Bean
    fun filterChain(
        http: HttpSecurity,
        customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
    ): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .cors {  }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/login",
                    "/api/v1/buyer/user_signup",
                    "/api/v1/seller/user_signup",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/api/v1/**", // 구현 완료 후 삭제
                    "/oauth/login/**",
                    "/api/v1/emails/**",
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntrypoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        configuration.allowedOrigins = listOf("https://www.highvecommerce.com", "http://localhost:5173")   // 허용할 URL

        configuration.allowedMethods = listOf("POST", "GET", "DELETE", "PUT", "PATCH")  // 허용할 Method

        configuration.allowedHeaders = listOf("*") // 허용할 Header

        configuration.allowCredentials = true // 세션 쿠키를 유지할 지 여부

        val source = UrlBasedCorsConfigurationSource() // cors 적용
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

}