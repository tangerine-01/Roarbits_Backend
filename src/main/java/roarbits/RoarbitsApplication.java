package roarbits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class RoarbitsApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoarbitsApplication.class, args);
    }
}