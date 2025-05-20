package br.com.tiinforma.backend.controller.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.VideoUploadDTO;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private ObjectMapper objectMapper;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("descricao") String descricao,
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "palavra_chave", required = false) String palavra_chave,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        log.info("Recebida requisição de upload de vídeo para o usuário: {}", userDetails.getUsername());
        log.info("Título do vídeo: {}", titulo);
        log.info("Descrição do vídeo: {}", descricao);
        log.info("Categoria do vídeo: {}", categoria);
        log.info("JSON de palavras-chave recebido: {}", palavra_chave);

        List<String> palavraChave = Collections.emptyList();
        try {
            if (palavra_chave != null) {
                palavraChave = objectMapper.readValue(palavra_chave, new TypeReference<List<String>>() {});
                log.info("Palavras-chave desserializadas: {}", palavraChave);
            } else {
                log.info("Nenhuma palavra-chave fornecida.");
            }

            Criador criador = criadorRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> {
                        log.error("Criador não encontrado para o email: {}", userDetails.getUsername());
                        return new RuntimeException("Criador não encontrado");
                    });

            VideoUploadDTO dto = new VideoUploadDTO(
                    file,
                    titulo,
                    descricao,
                    categoria,
                    LocalDate.now(),
                    palavraChave
            );

            String response = storageService.uploadFile(
                    dto.getFile(),
                    dto.getTitulo(),
                    dto.getDescricao(),
                    dto.getCategoria(),
                    dto.getDataCadastro(),
                    String.join(",", dto.getPalavraChave()),
                    criador
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (JsonProcessingException e) {
            log.error("Erro ao processar JSON de palavras-chave: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao processar palavras-chave: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro durante o upload do vídeo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no upload do vídeo: " + e.getMessage());
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
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        String mensagem = storageService.deleteFile("Arquivo:" + fileName,"Deletado pelo usuario:" + userDetails.getUsername());
        return ResponseEntity.ok(mensagem);
    }
}