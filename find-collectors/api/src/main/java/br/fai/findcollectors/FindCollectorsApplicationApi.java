package br.fai.findcollectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EntityScan("br.fai.findcollectors.entities")
@EnableJpaAuditing
@SpringBootApplication
public class FindCollectorsApplicationApi {
    public static void main(String[] args) {
        SpringApplication.run(FindCollectorsApplicationApi.class, args);
    }
}
