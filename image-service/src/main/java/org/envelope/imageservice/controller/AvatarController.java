package org.envelope.imageservice.controller;

import org.envelope.imageservice.config.ServiceFactory;
import org.envelope.imageservice.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final ImageService imageService;
    @Autowired
    public AvatarController(ServiceFactory serviceFactory,
                            @Value("${minio.bucket-names.avatar}") String bucketName) {
        this.imageService = serviceFactory.createService(bucketName);
    }

    @GetMapping(value = "/{name}",
            produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE,
                    MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public byte[] downloadAvatar(@PathVariable String name) {
        return imageService.downloadImage(name);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String uploadAvatar(@RequestPart("imageFile") MultipartFile imageFile) {
        return imageService.uploadImage(imageFile);
    }

    @PutMapping(value = "/{name}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateAvatar(@PathVariable String name, @RequestPart("imageFile") MultipartFile imageFile) {
        imageService.updateImage(name, imageFile);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAvatar(@PathVariable String name) {
        imageService.removeImage(name);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void clearBucket() {
        imageService.clearBucket();
    }
}
