package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoResponseDto;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.AvaliacaoRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.AvaliacaoService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AvaliacaoImpl implements AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideoRepository videoRepository;
    private final ModelMapper modelMapper;


    @Override
    public AvaliacaoResponseDto findByUsuarioAndVideo(Long usuarioId, Long videoId) {
        return avaliacaoRepository.findByUsuarioIdAndVideoId(usuarioId, videoId)
                .map(avaliacao -> modelMapper.map(avaliacao, AvaliacaoResponseDto.class))
                .orElse(null);
    }

    @Override
    public AvaliacaoResponseDto create(AvaliacaoCreateDto dto) {
        if (avaliacaoRepository.existsByUsuarioIdAndVideoId(dto.getUserId(), dto.getVideoId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Você já avaliou este vídeo anteriormente"
            );
        }

        Usuario usuario = usuarioRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Video video = videoRepository.findById(dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(dto.getNota())
                .comentario(dto.getComentario())
                .usuario(usuario)
                .video(video)
                .build();

        Avaliacao saved = avaliacaoRepository.save(avaliacao);
        return modelMapper.map(saved, AvaliacaoResponseDto.class);
    }

    @Override
    public AvaliacaoResponseDto findById(Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));
        return modelMapper.map(avaliacao, AvaliacaoResponseDto.class);
    }

    @Override
    public List<AvaliacaoResponseDto> findAll() {
        return avaliacaoRepository.findAll()
                .stream()
                .map(avaliacao -> modelMapper.map(avaliacao, AvaliacaoResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AvaliacaoCreateDto update(AvaliacaoCreateDto dto) {
        Avaliacao avaliacao = avaliacaoRepository.findByUsuarioIdAndVideoId(dto.getUserId(), dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        Avaliacao updated = avaliacaoRepository.save(avaliacao);
        return modelMapper.map(updated, AvaliacaoCreateDto.class);
    }

    @Override
    public void delete(Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));
        avaliacaoRepository.delete(avaliacao);
    }
}