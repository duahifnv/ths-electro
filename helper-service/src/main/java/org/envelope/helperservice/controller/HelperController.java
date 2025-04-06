package org.envelope.helperservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.envelope.helperservice.dto.HelperDto;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.service.HelperService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class HelperController {
    private final HelperService helperService;
    @Operation(summary = "Получить список всех помощников")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Helper> findAllHelpers(@RequestParam(defaultValue = "0") @Min(0) Integer pageNumber,
                                       @RequestParam(defaultValue = "5") @Min(3) Integer pageSize) {
        return helperService.findAllHelpers(pageNumber, pageSize).stream().toList();
    }
    @Operation(summary = "Получить помощника по ID телеграмма")
    @GetMapping("/{tgId}")
    @ResponseStatus(HttpStatus.OK)
    public Helper findByTgId(@PathVariable String tgId) {
        return helperService.findByTgId(tgId);
    }
    @Operation(summary = "Добавить помощника с ID телеграмма")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createHelper(@Valid @RequestBody HelperDto helperDto) {
        helperService.createHelper(helperDto);
    }
    @Operation(summary = "Изменить помощника по ID телеграмма")
    @PutMapping("/{tgId}")
    @ResponseStatus(HttpStatus.OK)
    public void updateHelper(@PathVariable String tgId, @Valid @RequestBody HelperDto helperDto) {
        helperService.updateHelper(tgId, helperDto);
    }
    @Operation(summary = "Удалить помощника по ID телеграмма")
    @DeleteMapping("/{tgId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteHelper(@PathVariable String tgId) {
        helperService.deleteHelper(tgId);
    }
}
