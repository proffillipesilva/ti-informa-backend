package br.com.tiinforma.backend.services.implementations;

import br.com.tiinforma.backend.domain.UsuarioAvaliacao.UsuarioAvaliacao;
import br.com.tiinforma.backend.domain.avaliacao.Avaliacao;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoCreateDto;
import br.com.tiinforma.backend.domain.avaliacao.AvaliacaoResponseDto;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.AvaliacaoRepository;
import br.com.tiinforma.backend.repositories.UsuarioAvaliacaoRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.AvaliacaoService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AvaliacaoImpl implements AvaliacaoService {
    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VideoRepository videoRepository;
    private final UsuarioAvaliacaoRepository usuarioAvaliacaoRepository;
    private final ModelMapper modelMapper;

    @Override
    public AvaliacaoResponseDto findByUsuarioAndVideo(Long usuarioId, Long videoId) {
        UsuarioAvaliacao usuarioAvaliacao = usuarioAvaliacaoRepository.findByUsuarioIdAndVideoId(usuarioId, videoId)
                .orElse(null);

        if (usuarioAvaliacao == null || usuarioAvaliacao.getAvaliacao() == null) {
            return null;
        }

        AvaliacaoResponseDto response = modelMapper.map(usuarioAvaliacao.getAvaliacao(), AvaliacaoResponseDto.class);
        response.setDataAvaliacao(usuarioAvaliacao.getDataAvaliacao());
        return response;
    }

    @Override
    @Transactional
    public AvaliacaoResponseDto create(AvaliacaoCreateDto dto) {
        if (usuarioAvaliacaoRepository.existsByUsuarioIdAndVideoId(dto.getUserId(), dto.getVideoId())) {
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

        Avaliacao savedAvaliacao = avaliacaoRepository.save(avaliacao);

        UsuarioAvaliacao usuarioAvaliacao = UsuarioAvaliacao.builder()
                .usuario(usuario)
                .video(video)
                .avaliacao(savedAvaliacao)
                .build();

        usuarioAvaliacaoRepository.save(usuarioAvaliacao);

        atualizarMediaAvaliacoesVideo(video.getId());

        AvaliacaoResponseDto response = modelMapper.map(savedAvaliacao, AvaliacaoResponseDto.class);
        response.setDataAvaliacao(usuarioAvaliacao.getDataAvaliacao());
        return response;
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
    @Transactional
    public AvaliacaoCreateDto update(AvaliacaoCreateDto dto) {
        Avaliacao avaliacao = avaliacaoRepository.findByUsuarioIdAndVideoId(dto.getUserId(), dto.getVideoId())
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());

        Avaliacao updated = avaliacaoRepository.save(avaliacao);

        atualizarMediaAvaliacoesVideo(avaliacao.getVideo().getId());

        return modelMapper.map(updated, AvaliacaoCreateDto.class);
    }

    @Override
    @Transactional
    public void delete(Long id, Long userIdAutenticado) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliação não encontrada"));

        if (!avaliacao.getUsuario().getId().equals(userIdAutenticado)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem permissão para deletar esta avaliação"
            );
        }

        Long videoId = avaliacao.getVideo().getId();

        List<UsuarioAvaliacao> usuarioAvaliacoes = usuarioAvaliacaoRepository.findByAvaliacaoId(id);
        usuarioAvaliacaoRepository.deleteAll(usuarioAvaliacoes);

        avaliacaoRepository.delete(avaliacao);

        atualizarMediaAvaliacoesVideo(videoId);
    }

    private void atualizarMediaAvaliacoesVideo(Long videoId) {
        Double media = avaliacaoRepository.calcularMediaAvaliacoes(videoId);
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Vídeo não encontrado"));

        video.setAvaliacaoMedia(media != null ? media : 0.0);
        videoRepository.save(video);
    }

    @Override
    @Transactional
    public void atualizarMediaAvaliacoes(Long videoId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findByVideoId(videoId);

        if (!avaliacoes.isEmpty()) {
            double media = avaliacoes.stream()
                    .mapToDouble(Avaliacao::getNota)
                    .average()
                    .orElse(0.0);

            Video video = videoRepository.findById(videoId)
                    .orElseThrow(() -> new EntityNotFoundException("Vídeo não encontrado"));

            video.setMediaAvaliacao(media);
            videoRepository.save(video);
        }
    }
}