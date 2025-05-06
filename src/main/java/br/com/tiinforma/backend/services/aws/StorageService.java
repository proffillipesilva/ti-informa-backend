/*package br.com.tiinforma.backend.services.aws;

import br.com.tiinforma.backend.domain.criador.Criador;
import br.com.tiinforma.backend.domain.video.Video;
import br.com.tiinforma.backend.repositories.VideoRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public String uploadFile(MultipartFile file, String titulo, String descricao, String categoria, List<String> palavraChave, Criador criador) {
        File fileObj = convertMultiPartFileToFile(file);
        String fileNameOnS3 = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        s3Client.putObject(new PutObjectRequest(bucketName, fileNameOnS3, fileObj));
        fileObj.delete();

        Video video = Video.builder()
                .titulo(titulo)
                .descricao(descricao)
                .url(fileNameOnS3)
                .dataPublicacao(LocalDate.now())
                .categoria(categoria)
                .palavraChave(palavraChave)
                .criador(criador)
                .build();
        videoRepository.save(video);

        return "File uploaded successfully. S3 key: " + fileNameOnS3 + ", Video ID: " + video.getId();
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

    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName,fileName);
        return "File deleted " + fileName;
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
*/

