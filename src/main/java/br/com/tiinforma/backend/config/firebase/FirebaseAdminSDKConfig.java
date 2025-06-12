package br.com.tiinforma.backend.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseAdminSDKConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseAdminSDKConfig.class);

    @Bean
    public FirebaseApp initializeFirebaseApp() {
        try {
            logger.info("Tentando inicializar o Firebase Admin SDK...");

            ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");

            if (!resource.exists()) {
                logger.error("ERRO: O arquivo 'serviceAccountKey.json' NÃO foi encontrado no classpath (src/main/resources).");
                logger.error("Por favor, verifique se o arquivo está na pasta correta do seu projeto.");
                throw new IOException("Arquivo de credenciais Firebase não encontrado.");
            }

            try (InputStream serviceAccount = resource.getInputStream()) {
                logger.info("Arquivo 'serviceAccountKey.json' encontrado e sendo carregado.");

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                if (FirebaseApp.getApps().isEmpty()) {
                    logger.info("Nenhuma instância do FirebaseApp existente. Inicializando nova instância...");
                    return FirebaseApp.initializeApp(options);
                } else {
                    logger.info("Instância do FirebaseApp já existente. Usando a instância existente.");
                    return FirebaseApp.getInstance();
                }
            }
        } catch (IOException e) {
            logger.error("Erro fatal ao inicializar o Firebase Admin SDK: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na inicialização do Firebase Admin SDK.", e);
        } catch (Exception e) {
            logger.error("Erro inesperado durante a inicialização do Firebase Admin SDK: {}", e.getMessage(), e);
            throw new RuntimeException("Erro inesperado na inicialização do Firebase Admin SDK.", e);
        }
    }
}
