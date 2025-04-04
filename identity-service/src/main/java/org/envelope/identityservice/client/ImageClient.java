package org.envelope.identityservice.client;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface ImageClient {
    @PostExchange(url = "/api/images/avatars",
            contentType = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadAvatar(@RequestPart("imageFile") MultipartFile imageFile);
    @DeleteExchange(url = "/api/images/avatars/{name}")
    void deleteAvatar(@PathVariable String name);
}
