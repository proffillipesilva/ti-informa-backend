package br.com.tiinforma.backend.services.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.VideoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StorageService {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private VideoRepository videoRepository;

    @Transactional
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Por favor, envie um vídeo.");
        }

        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, fileObj);
            s3Client.putObject(putObjectRequest);
            return fileName;
        } finally {
            if (fileObj.exists()) {
                fileObj.delete();
            }
        }
    }

    @Transactional
    public String uploadFile(
            MultipartFile file,
            String titulo,
            String descricao,
            String categoria,
            LocalDate dataCadastro,
            String palavraChaveString,
            Criador criador
    ) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode estar vazio");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IllegalArgumentException("Tipo de arquivo inválido. Por favor, envie um vídeo.");
        }

        File fileObj = convertMultiPartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("video/mp4");

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, fileObj);
            putObjectRequest.setMetadata(metadata);

            s3Client.putObject(putObjectRequest);


            List<String> palavrasChaveList = null;
            if (palavraChaveString != null && !palavraChaveString.trim().isEmpty()) {
                palavrasChaveList = Arrays.stream(palavraChaveString.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
            }

            log.info("Salvando vídeo com palavras-chave: {}", palavrasChaveList);
            Video video = Video.builder()
                    .titulo(titulo)
                    .descricao(descricao)
                    .categoria(categoria)
                    .palavra_chave(palavrasChaveList != null ? String.join(",", palavrasChaveList) : null)
                    .dataPublicacao(dataCadastro != null ? dataCadastro : LocalDate.now())
                    .key(fileName)
                    .criador(criador)
                    .build();
            log.info("Video a ser salvo: {}", video);

            videoRepository.save(video);

            return "File uploaded and video saved with key: " + fileName;
        } finally {
            if (fileObj.exists()) {
                fileObj.delete();
            }
        }
    }


    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName, fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Erro ao fazer download do arquivo {}", fileName, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao baixar o arquivo");
        }
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


    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Erro ao converter MultipartFile para File", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar o arquivo");
        }
        return convertFile;
    }
}