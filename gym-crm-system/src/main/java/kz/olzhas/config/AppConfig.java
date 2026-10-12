package kz.olzhas.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"kz.olzhas.service", "kz.olzhas.dao"})
public class AppConfig {
}
