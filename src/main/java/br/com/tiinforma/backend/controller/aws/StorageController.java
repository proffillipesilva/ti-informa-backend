/*package br.com.tiinforma.backend.controller.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.services.aws.StorageService;
import br.com.tiinforma.backend.services.interfaces.CriadorService;
import br.com.tiinforma.backend.services.interfaces.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService storageService;

    @Autowired
    private CriadorService criadorService;

    @Autowired
    private VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVideo(
            @RequestPart("file") MultipartFile file,
            @RequestParam("titulo") String titulo,
            @RequestParam("descricao") String descricao,
            @RequestParam("categoria") String categoria,
            @RequestParam(value = "palavraChave", required = false) List<String> palavraChave
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Criador criador = criadorService.buscarPorEmail(userDetails.getUsername());
            if (criador != null) {
                String uploadResult = storageService.uploadFile(file, titulo, descricao, categoria, palavraChave, criador);
                return ResponseEntity.status(HttpStatus.CREATED).body(uploadResult);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Criador não encontrado.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
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

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<String> deleteVideo(@PathVariable Long videoId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Criador criadorAutenticado = criadorService.buscarPorEmail(userDetails.getUsername()); // Adapte a busca conforme sua lógica

            if (criadorAutenticado != null) {
                boolean deletado = videoService.deleteVideo(videoId, criadorAutenticado);
                if (deletado) {
                    return ResponseEntity.ok("Vídeo deletado com sucesso.");
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para deletar este vídeo.");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Criador não autenticado.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
    }
}
*/
