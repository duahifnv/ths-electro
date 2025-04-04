package org.envelope.identityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.identityservice.client.ImageClient;
import org.envelope.identityservice.entity.User;
import org.envelope.identityservice.exception.ServerException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис аватарок пользователей")
public class AvatarService {
    private final ImageClient imageClient;
    private final UserService userService;
    @Transactional
    public void updateAvatar(@RequestPart("imageFile") MultipartFile imageFile, String username) {
        User user = userService.findByUsername(username);
        try {
            if (user.getAvatarId() != null) {
                deleteUserAvatar(user);
            }
            String avatarId = imageClient.uploadAvatar(imageFile);
            user.setAvatarId(avatarId);
            userService.saveUser(user);
        }
        catch (Exception e) {
            log.error("Image client error: ", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Transactional
    public void deleteAvatar(String username) {
        User user = userService.findByUsername(username);
        deleteUserAvatar(user);
        userService.saveUser(user);
    }
    private void deleteUserAvatar(User user) {
        if (user.getAvatarId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Невозможно удалить отсутствующую аватарку");
        }
        try {
            imageClient.deleteAvatar(user.getAvatarId());
            user.setAvatarId(null);
        } catch (Exception e) {
            log.error("Image client http error: {}", e.getMessage());
            throw new ServerException(e);
        }
    }
}
