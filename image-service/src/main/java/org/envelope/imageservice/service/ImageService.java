package org.envelope.imageservice.service;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.envelope.imageservice.exception.ResourceNotFoundException;
import org.envelope.imageservice.exception.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j(topic = "Сервис изображений")
public class ImageService {
    @Autowired
    private MinioClient minioClient;
    private final String bucketName;
    public ImageService(String bucketName) {
        this.bucketName = bucketName;
    }
    public byte[] downloadImage(String name) {
        if (!imageExists(name))
            throw new ResourceNotFoundException();
        byte[] imageBytes;
        GetObjectResponse objectResponse;
        try {
            objectResponse = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build()
            );
        }
        catch (Exception e) {
            throw new ServerException(e);
        }
        try (InputStream stream = objectResponse) {
            imageBytes = stream.readAllBytes();
        } catch (IOException e) {
            log.error("Ошибка считывания S3 объекта: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        log.info("Успешно получен из бакета {} S3 объект: {}", bucketName,
                objectResponse.object());
        return imageBytes;
    }
    public String uploadImage(MultipartFile imageFile) {
        String filename = imageFile.getOriginalFilename();
        if (filename == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Невозможно получить имя файла");
        }
        filename = filename.replaceAll("\\s+", "");
        if (imageExists(filename)) { // Если изображение с таким именем уже занято
            filename = generateUniqueAvatarId(imageFile); // Генерируем уникальное имя на основе имени файла
        }
        return uploadFile(filename, imageFile);
    }

    public void updateImage(String name, MultipartFile imageFile) {
        if (!imageExists(name)) { // Если изображения с таким именем не существует
            throw new ResourceNotFoundException();
        }
        uploadFile(name, imageFile);
    }
    public void removeImage(String name) {
        if (!imageExists(name))
            throw new ResourceNotFoundException();
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build());
            log.info("Успешно удален из бакета {} объект: {}", bucketName, name);
        } catch (Exception e) {
            log.error("Ошибка при удалении из S3 хранилища: {}", e.getMessage());
            throw new ResourceNotFoundException();
        }
    }
    private String generateUniqueAvatarId(MultipartFile imageFile) {
        String shortenUUID = UUID.randomUUID().toString().substring(0, 13);
        return imageFile.getOriginalFilename() + "_" + shortenUUID;
    }
    private String uploadFile(String name, MultipartFile file) {
        try (InputStream stream = file.getInputStream()) {
            var object = minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .stream(stream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            log.info("Успешно загружен в бакет {} файл: {}", bucketName,
                    name);
            return object.object(); // Возвращаем имя объекта
        } catch (IOException e) {
            log.error("Ошибка считывания клиентского файла {}: {}",
                    file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    private boolean imageExists(String name) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
    public void clearBucket() {
        var objects = minioClient.listObjects(ListObjectsArgs.builder()
                .bucket(bucketName)
                .build());
        try {
            for (var object : objects) {
                String objectName = object.get().objectName();
                removeImage(objectName);
            }
            log.info("Бакет {} успешно очищен", bucketName);
        } catch (Exception e) {
            throw new ServerException(e);
        }
    }
}
