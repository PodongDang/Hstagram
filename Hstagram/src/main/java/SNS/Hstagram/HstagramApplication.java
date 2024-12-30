package SNS.Hstagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableCaching
//@ComponentScan(basePackages = "SNS.Hstagram.controller")
public class HstagramApplication {

	public static void main(String[] args) {
		SpringApplication.run(HstagramApplication.class, args);
	}

}
