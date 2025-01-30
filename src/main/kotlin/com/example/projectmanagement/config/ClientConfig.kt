package com.example.projectmanagement.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ClientConfig {
    // TODO Configure proper rest template with timeout settings
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()
}
