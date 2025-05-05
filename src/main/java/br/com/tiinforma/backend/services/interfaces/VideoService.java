package br.com.tiinforma.backend.services.interfaces;

import br.com.tiinforma.backend.domain.criador.Criador;

public interface VideoService {

    boolean deleteVideo(Long videoId, Criador criadorAutenticado);
}
