package ca.gc.dfo.slims.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "ca.gc.dfo.slims")
@EntityScan(basePackages = "ca.gc.dfo.slims")
@ComponentScan(basePackages = {"ca.gc.dfo.slims", "ca.gc.dfo.spring_commons.commons_offline_wet"})
@EnableSpringDataWebSupport
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
public class ApplicationConfig {

}