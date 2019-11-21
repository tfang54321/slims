package ca.gc.dfo.slims;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).apiInfo(this.getApiInfo()).select()
				.apis(RequestHandlerSelectors.any()).paths(PathSelectors.any()).build();

	}

	private ApiInfo getApiInfo() {
		ApiInfo apiInfo = new ApiInfoBuilder().title("Slims Controller Or Service API definition").description("API")
				.version("1.0").contact(new Contact("wen", "", "wen.fang@dfo-mpo.gc.ca")).build();

		return apiInfo;
	}

}
