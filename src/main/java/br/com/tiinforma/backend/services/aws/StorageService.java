package br.com.tiinforma.backend.services.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.enums.Funcao;
import br.com.tiinforma.backend.domain.usuario.Usuario;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.UsuarioRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import br.com.tiinforma.backend.services.interfaces.FotoAtualizavel;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class StorageService  {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public Video uploadFile(
            MultipartFile file,
            String titulo,
            String descricao,
            String categoria,
            LocalDate dataPublicacao,
            List<String> palavraChave,
            Criador criador
    ) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();

        Video video = Video.builder()
                .titulo(titulo)
                .descricao(descricao)
                .categoria(categoria)
                .palavraChave(palavraChave)
                .dataPublicacao(dataPublicacao != null ? dataPublicacao : LocalDate.now())
                .key(fileName)
                .criador(criador)
                .build();

        return videoRepository.save(video);
    }


    public <T extends FotoAtualizavel> String uploadFoto(
            MultipartFile file,
            Long id,
            JpaRepository<T, Long> repository
    ){
        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();

        String fileUrl = s3Client.getUrl(bucketName, fileName).toString();

        T entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entidade não encontrada"));

        entity.setFotoUrl(fileUrl);
        repository.save(entity);

        return "Foto enviada e entidade atualizada com URL: " + fileUrl;
    }

    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public String deleteFile(String fileName, String username) {
        Video video = videoRepository.findByKey(fileName);

        if (video == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vídeo não encontrado");
        }

        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        boolean isAdmin = usuario.getFuncao() == Funcao.ADMINISTRADOR;

        if (!video.getCriador().getEmail().equals(username) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este vídeo");
        }

        s3Client.deleteObject(bucketName, fileName);
        videoRepository.delete(video);

        return "Vídeo excluído: " + fileName;
    }




    private File convertMultiPartFileToFile(MultipartFile file){
        File convertFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertFile)){
            fos.write(file.getBytes());
        }
        catch (IOException e){
            log.error("Erro ao converter multiplos arquivos" + e);
        }
        return convertFile;
    }
}
