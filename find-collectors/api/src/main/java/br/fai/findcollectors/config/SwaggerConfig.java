package br.fai.findcollectors.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Find Collectors API")
                        .version("1.0")
                        .description("API para gerenciamento de coleta reciclável"));
    }

    @Bean
    public GroupedOpenApi publicApi(){

        return GroupedOpenApi
                .builder()
                .group("find-collectors-api")
                .pathsToMatch("/api/**")
                .build();

    }
}