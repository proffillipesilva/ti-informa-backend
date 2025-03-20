package br.com.tiinforma.backend.config.db;


import br.com.tiinforma.backend.services.db.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevConfig {

    @Autowired
    private DBService dbService;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String value;

    @Bean
    public Boolean initDatabase() {
        if (value.equals("create")) {
            this.dbService.initDatabase();
        }
        return false;
    }
}
