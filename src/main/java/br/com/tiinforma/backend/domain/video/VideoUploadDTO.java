package br.com.tiinforma.backend.domain.video;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadDTO {
    private MultipartFile file;
    private MultipartFile thumbnail;
    private String titulo;
    private String descricao;
    private String categoria;
    private LocalDate dataCadastro;
    private List<String> palavraChave;
}

