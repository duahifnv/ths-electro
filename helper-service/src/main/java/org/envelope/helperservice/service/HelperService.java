package org.envelope.helperservice.service;

import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.repository.HelperRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelperService {
    private final HelperRepository helperRepository;
    public Helper findByTgId(String tgId) {
        return helperRepository.findByTgId(tgId)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
