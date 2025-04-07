package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.assinatura.Assinatura;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaCreateDto;
import br.com.tiinforma.backend.domain.assinatura.AssinaturaDto;
import br.com.tiinforma.backend.domain.enums.Plano;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.mapper.DozerMapper;
import br.com.tiinforma.backend.repositories.AssinaturaRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.interfaces.AssinaturaService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class AssinaturaImpl implements AssinaturaService {

    @Autowired
    private final AssinaturaRepository assinaturaRepository;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Override
    public AssinaturaDto findById(Long id) {
        var assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada: " + id));
        return DozerMapper.parseObject(assinatura, AssinaturaDto.class);
    }

    @Override
    public List<AssinaturaDto> findAll() {
        List<Assinatura> assinaturas = assinaturaRepository.findAll();
        return DozerMapper.parseListObject(assinaturas, AssinaturaDto.class);
    }

    @Override
    public AssinaturaDto create(AssinaturaCreateDto assinaturaCreateDto) {
        var usuario = usuarioRepository.findById(assinaturaCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + assinaturaCreateDto.getUserId()));

        Assinatura assinatura = new Assinatura();
        assinatura.setUsuario(usuario);
        assinatura.setPlano(Plano.valueOf(assinaturaCreateDto.getPlano().toUpperCase()));
        assinatura.setDataInicio(assinaturaCreateDto.getDataInicio() != null ? assinaturaCreateDto.getDataInicio() : LocalDate.now());
        assinatura.setDataFim(assinaturaCreateDto.getDataFim());
        assinatura.setPreco(assinaturaCreateDto.getPreco());

        assinatura = assinaturaRepository.save(assinatura);
        return DozerMapper.parseObject(assinatura, AssinaturaDto.class);
    }

    @Override
    @Transactional
    public AssinaturaCreateDto update(AssinaturaCreateDto assinaturaCreateDto) {
        var assinatura = assinaturaRepository.findById(assinaturaCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada"));

        assinatura.setPlano(Plano.valueOf(assinaturaCreateDto.getPlano().toUpperCase()));
        assinatura.setDataInicio(assinaturaCreateDto.getDataInicio());
        assinatura.setDataFim(assinaturaCreateDto.getDataFim());
        assinatura.setPreco(assinaturaCreateDto.getPreco());

        var assinaturaAtualizada = assinaturaRepository.save(assinatura);
        return DozerMapper.parseObject(assinaturaAtualizada, AssinaturaCreateDto.class);
    }

    @Override
    public void delete(Long id) {
        var assinatura = assinaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assinatura não encontrada: " + id));
        assinaturaRepository.delete(assinatura);
    }
}
