package br.com.tiinforma.backend.config.db;


import br.com.tiinforma.backend.services.db.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfig {

    @Autowired
    private DBService dbService;

    @Bean
    public Boolean initDatabase() {
        this.dbService.initDatabase();
        return true;
    }
}
