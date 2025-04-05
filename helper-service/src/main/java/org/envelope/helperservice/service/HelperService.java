package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.exception.IllegalClientException;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.repository.HelperRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelperService {
    private final HelperRepository helperRepository;
    public boolean existsByTgId(String tgId) {
        return helperRepository.existsByTgId(tgId);
    }
    public Helper findByTgId(String tgId) {
        return helperRepository.findByTgId(tgId)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
