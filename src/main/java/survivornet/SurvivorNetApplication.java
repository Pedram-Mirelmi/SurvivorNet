package survivornet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EntityScan(basePackages = "survivornet.models")
@EnableTransactionManagement
public class SurvivorNetApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurvivorNetApplication.class, args);
	}

}
