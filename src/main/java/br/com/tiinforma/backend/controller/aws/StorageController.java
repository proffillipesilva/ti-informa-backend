package br.com.tiinforma.backend.controller.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.userDetails.UserDetailsImpl;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.VideoUploadDTO;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.services.aws.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(
            @ModelAttribute VideoUploadDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Long idCriador = userDetails.getId();

        Criador criador = criadorRepository.findById(idCriador)
                .orElseThrow(() -> new ResourceNotFoundException("Criador não encontrado"));

        String response = storageService.uploadFile(
                dto.getFile(),
                dto.getTitulo(),
                dto.getDescricao(),
                dto.getCategoria(),
                dto.getDataCadastro(),
                dto.getPalavraChave(),
                criador
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
