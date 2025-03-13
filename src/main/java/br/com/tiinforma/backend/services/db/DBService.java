package br.com.tiinforma.backend.services.db;

import br.com.tiinforma.backend.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DBService {

    @Autowired
    AssinaturaRepository assinaturaRepository;

    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @Autowired
    CriadorRepository criadorRepository;

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    PlaylistVideoRepository playlistVideoRepository;

    @Autowired
    Usuario usuarioReposiroty;

    @Autowired
    UsuarioVideoProgressoRepository usuarioVideoProgressoRepository;

    @Autowired
    VideoRepository videoRepository;




}
