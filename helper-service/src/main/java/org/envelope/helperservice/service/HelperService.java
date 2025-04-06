package org.envelope.helperservice.service;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.envelope.helperservice.dto.HelperDto;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.exception.HelperNotFoundException;
import org.envelope.helperservice.exception.ResourceNotFoundException;
import org.envelope.helperservice.repository.HelperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "Сервис управления помощниками")
public class HelperService {
    private final HelperRepository helperRepository;
    public boolean existsByTgId(String tgId) {
        return helperRepository.existsByTgId(tgId);
    }
    public Page<Helper> findAllHelpers(@Min(0) Integer pageNumber, Integer pageSize) {
        return helperRepository.findAll(getPageRequest(pageNumber, pageSize));
    }
    public Helper findByTgId(String tgId) {
        return helperRepository.findByTgId(tgId)
                .orElseThrow(HelperNotFoundException::new);
    }
    @Transactional
    public void createHelper(HelperDto helperDto) {
        Helper helper = new Helper();
        helper.setTgId(helperDto.tgId());
        helper.setFirstname(helperDto.firstname());
        helper.setLastname(helperDto.lastname());
        log.info("Создан новый помощник tg@{}", helperDto.tgId());
        saveHelper(helper);
    }
    @Transactional
    public void saveHelper(Helper helper) {
        helperRepository.save(helper);
        log.info("Помощник tg@{} сохранен в базу данных", helper.getTgId());
    }
    @Transactional
    public void updateHelper(String tgId, HelperDto helperDto) {
        Helper helper = findByTgId(tgId);
        helper.setTgId(helperDto.tgId());
        helper.setFirstname(helperDto.firstname());
        helper.setLastname(helperDto.lastname());
        log.info("Обновлен помощник tg@{}", helperDto.tgId());
        saveHelper(helper);
    }
    @Transactional
    public void deleteHelper(String tgId) {
        Helper helper = findByTgId(tgId);
        helperRepository.delete(helper);
        log.info("Удален помощник tg@{}", tgId);
    }
    private PageRequest getPageRequest(Integer pageNumber, Integer pageSize) {
        return PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "id"));
    }
}
