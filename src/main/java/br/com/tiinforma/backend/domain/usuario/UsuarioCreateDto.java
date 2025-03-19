package br.com.tiinforma.backend.domain.usuario;

import br.com.tiinforma.backend.domain.assinatura.AssinaturaDto;
import br.com.tiinforma.backend.domain.playlist.PlaylistDto;

import java.util.List;

public record UsuarioCreateDto(
        String nome,
        String email,
        String password,
        String interesses,
        List<PlaylistDto> playlistDtos,
        List<AssinaturaDto> assinaturaDtos
) {
}
