package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoDto;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.mapper.DozerMapper;
import br.com.tiinforma.backend.repositories.AvaliacaoRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.interfaces.AvaliacaoService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AvaliacaoImpl implements AvaliacaoService {

    @Autowired
    private final AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Override
    public AvaliacaoDto findById(Long id) {
        var avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada: " + id));
        return DozerMapper.parseObject(avaliacao, AvaliacaoDto.class);
    }

    @Override
    public List<AvaliacaoDto> findAll() {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAll();
        return DozerMapper.parseListObject(avaliacoes, AvaliacaoDto.class);
    }

    @Override
    public AvaliacaoDto create(AvaliacaoCreateDto avaliacaoCreateDto) {
        var usuario = usuarioRepository.findById(avaliacaoCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: " + avaliacaoCreateDto.getUserId()));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setId(usuario.getId());
        avaliacao.setNota(avaliacaoCreateDto.getNota());
        avaliacao.setComentario(avaliacaoCreateDto.getComentario());

        avaliacao = avaliacaoRepository.save(avaliacao);
        return DozerMapper.parseObject(avaliacao, AvaliacaoDto.class);
    }

    @Override
    @Transactional
    public AvaliacaoCreateDto update(AvaliacaoCreateDto avaliacaoCreateDto) {
        var avaliacao = avaliacaoRepository.findById(avaliacaoCreateDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        avaliacao.setNota(avaliacaoCreateDto.getNota());
        avaliacao.setComentario(avaliacaoCreateDto.getComentario());

        var avaliacaoAtualizada = avaliacaoRepository.save(avaliacao);
        return DozerMapper.parseObject(avaliacaoAtualizada, AvaliacaoCreateDto.class);
    }

    @Override
    public void delete(Long id) {
        var avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada: " + id));
        avaliacaoRepository.delete(avaliacao);
    }
}
