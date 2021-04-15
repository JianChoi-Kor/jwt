package com.example.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Errors;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket api1(HttpServletResponse response) {
        String version = "test";
        String title = "";
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .groupName(version)
                .consumes(getConsumeContentTypes())
                .produces(getProduceContentTypes())
                .select()
                .apis(
                        RequestHandlerSelectors.basePackage("com.example.jwt.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo(title, version))
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Collections.singletonList(apiKey()))
                .ignoredParameterTypes(Errors.class);

    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth()).forPaths(PathSelectors.any()).build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("golbal", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Arrays.asList(new SecurityReference("authorization", authorizationScopes));
    }



    private ApiInfo apiInfo(String title, String version) {
        return new ApiInfo(
                title,
                "REST API Swagger",
                version,
                "",
                new Contact("", "", ""),
                "",
                "",
                new ArrayList<>());
    }
    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add("application/json;charset=UTF-8");
        return produces;
    }
    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/x-www-form-urlencoded");
        consumes.add("multipart/form-data");
        return consumes;
    }
    private ApiKey apiKey() {
        return new ApiKey("authorization", "authorization", "header");
    }
}
