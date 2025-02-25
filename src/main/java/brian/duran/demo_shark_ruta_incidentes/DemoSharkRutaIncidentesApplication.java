package brian.duran.demo_shark_ruta_incidentes;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class DemoSharkRutaIncidentesApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoSharkRutaIncidentesApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenApi(){
		return new OpenAPI().info(new Info().title("Ruta Incidentes APP").version("1.0.0").description("Spring boot MongoDB y Redis con Swagger"));
	}
}
