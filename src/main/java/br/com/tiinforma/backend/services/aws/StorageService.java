package br.com.tiinforma.backend.services.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.exceptions.ResourceNotFoundException;
import br.com.tiinforma.backend.repositories.CriadorRepository;
import br.com.tiinforma.backend.repositories.VideoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private CriadorRepository criadorRepository;


    public String uploadFile(
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

        videoRepository.save(video);

        return "File uploaded and video saved with key: " + fileName;
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

        if (!video.getCriador().getEmail().equals(username)) {
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
