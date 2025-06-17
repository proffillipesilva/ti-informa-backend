package br.com.tiinforma.backend.config.mapper;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.criador.CriadorResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(Criador.class, CriadorResponseDto.class)
                .addMappings(mapper -> {
                    mapper.map(Criador::getTotalInscritos, CriadorResponseDto::setTotalInscritos);
                });

        return modelMapper;
    }
}
