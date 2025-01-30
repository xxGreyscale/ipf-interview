package com.example.projectmanagement

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(servers = [Server(url = "/", description = "Default Server URL")])
@SpringBootApplication
class ProjectmanagementApplication

fun main(args: Array<String>) {
	runApplication<ProjectmanagementApplication>(*args)
}
