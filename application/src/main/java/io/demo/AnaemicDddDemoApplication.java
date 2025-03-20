package io.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConfigurationPropertiesScan(basePackages = "io.demo.*")
@ComponentScan(basePackages = "io.demo.*")
@EntityScan(basePackages = "io.demo.*")
@EnableJpaRepositories(basePackages = "io.demo.*", considerNestedRepositories = true)
@EnableJpaAuditing
@SpringBootApplication
public class AnaemicDddDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AnaemicDddDemoApplication.class, args);
    }
}
