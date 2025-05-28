package br.com.tiinforma.backend.controller.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.domain.video.VideoResponseDto;
import br.com.tiinforma.backend.domain.video.VideoUploadDTO;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.aws.StorageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/file")
public class StorageController {

    private static final Logger log = LoggerFactory.getLogger(StorageController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private CriadorRepository criadorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("titulo") String titulo,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "palavra_chave", required = false) String palavra_chave,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("Recebida requisição de upload de vídeo para o usuário: {}", userDetails.getUsername());
        log.info("Tamanho do thumbnail: {}", thumbnail.getSize());

        List<String> palavraChave = Collections.emptyList();
        try {
            if (palavra_chave != null) {
                palavraChave = objectMapper.readValue(palavra_chave, new TypeReference<List<String>>() {});
            }

            Criador criador = criadorRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Criador não encontrado"));

            VideoUploadDTO dto = new VideoUploadDTO(
                    file,
                    thumbnail,
                    titulo,
                    descricao,
                    categoria,
                    LocalDate.now(),
                    palavraChave
            );

            String response = String.valueOf(storageService.uploadFile(
                    dto.getFile(),
                    dto.getThumbnail(),
                    dto.getTitulo(),
                    dto.getDescricao(),
                    dto.getCategoria(),
                    dto.getDataCadastro(),
                    String.join(",", dto.getPalavraChave()),
                    criador
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Erro durante o upload do vídeo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no upload do vídeo: " + e.getMessage());
        }
    }
    
    @GetMapping("/meus-videos")
    @Transactional
    public ResponseEntity<?> listarMeusVideos(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Criador criador = criadorRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Criador não encontrado"));
            List<Video> videos = videoRepository.findByCriadorId(criador.getId());
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar vídeos");
        }
    }


    @PostMapping("foto/{tipo}/{id}")
    public ResponseEntity<String> uploadFoto(
            @PathVariable String tipo,
            @PathVariable Long id,
            @RequestParam MultipartFile file
    ){
        String response;

        switch (tipo.toLowerCase()) {
            case "criador":
                response = storageService.uploadFoto(file, id, criadorRepository);
                break;
            case "usuario":
                response = storageService.uploadFoto(file, id, usuarioRepository);
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo inválido");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] data = storageService.downloadFile(fileName);
        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-Type", "application/octet-stream")
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(
            @PathVariable String fileName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String mensagem = storageService.deleteFile(fileName, userDetails.getUsername());
        return ResponseEntity.ok(mensagem);
    }






}
