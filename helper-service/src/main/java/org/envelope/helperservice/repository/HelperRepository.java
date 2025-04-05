package org.envelope.helperservice.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.envelope.helperservice.entity.Helper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HelperRepository extends JpaRepository<Helper, Long> {
    Optional<Helper> findByTgId(@Size(max = 100) @NotNull String tgId);
    boolean existsByTgId(@Size(max = 100) @NotNull String tgId);
}
