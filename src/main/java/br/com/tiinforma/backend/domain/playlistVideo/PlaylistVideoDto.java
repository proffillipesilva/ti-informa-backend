package br.com.tiinforma.backend.domain.playlistVideo;

public record PlaylistVideoDto(
        Long id,
        Long idVideo,
        String videoTitulo,
        Integer posicaoVideo
) {
}
