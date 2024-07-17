package org.demoshop.services;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.demoshop.dto.StandardResponseDto;
import org.demoshop.models.FileInfo;
import org.demoshop.repositories.FileInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesService {

    private final AmazonS3 amazonS3;
    private final FileInfoRepository repository;


    @Transactional
    public StandardResponseDto upload(MultipartFile file) throws IOException {

        String originalFileName = file.getOriginalFilename(); // получаем имя файла оригинальное

        String extension;

        if (originalFileName != null) {
            extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1); // получаем расширение
        } else {
            throw new IllegalArgumentException("null original file name");
        }

        String uuid = UUID.randomUUID().toString(); // генерация случайной строки в формате UUID
        String newFileName = uuid + "." + extension; // создаем новое имя файла

        // загрузка
        // String linkInfo = "Наш линк на загрузку: " + newFileName ;


        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType()); // сообщаем хранилищу какого типа у нас файл

        // создаем запрос на отправку файла

        PutObjectRequest request = new PutObjectRequest(
                "demo-shop-files",
                "image/" + newFileName,
                inputStream,
                metadata
        ).withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3.putObject(request);

        // получаем ссылку на файл
        String linkInfo = amazonS3.getUrl("demo-shop-files", "image/" + newFileName).toString();


        FileInfo fileInfo = FileInfo.builder()
                .link(linkInfo)
                .build();

        repository.save(fileInfo);
        /*
        добавить логику:
        - поскольку этот запрос сопровождается JWT то мы можем получить username
        - по username мы можем найти из базы - user
        - и у него в поле link - вставить ссылку на файл
         */


        return StandardResponseDto.builder()
                .message(linkInfo)
                .build();
    }
}
