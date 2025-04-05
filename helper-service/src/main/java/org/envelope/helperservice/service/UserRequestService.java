package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.entity.WaitingUserRequest;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.repository.UserRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRequestService {
    private final UserRequestRepository requestRepository;
    @Transactional
    public void save(WaitingUserRequest request) {
        List<WaitingUserRequest> requestList = requestRepository.findByUserId(request.getUserId());
        if (!requestList.isEmpty()) {
            requestRepository.deleteAllByUserId(request.getUserId());
        }
        requestRepository.save(request);
    }
    @Transactional
    public void deleteByUserId(Long userId) {
        if (!requestRepository.existsByUserId(userId)) {
            throw new ResourceNotFoundException();
        }
        requestRepository.deleteByUserId(userId);
    }
}
