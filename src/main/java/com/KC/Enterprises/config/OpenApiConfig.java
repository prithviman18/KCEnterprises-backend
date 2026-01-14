package com.KC.Enterprises.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "KC Enterprises API",
        version = "1.0",
        description = "API documentation for KC Enterprises Management System",
        contact = @Contact(
            name = "KC Enterprises Support",
            email = "support@kcenterprises.com"
        ),
        license = @License(name = "Apache 2.0", url = "http://springdoc.org")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Local Server")
    }
)
@SecurityScheme(
    name = "Bearer Authentication",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
public class OpenApiConfig {
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/**")
                .packagesToScan("com.KC.Enterprises.controller")
                .build();
    }
    
    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("authentication")
                .pathsToMatch("/auth/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi supplierApi() {
        return GroupedOpenApi.builder()
                .group("supplier")
                .pathsToMatch("/supplier/**")
                .build();
    }
}