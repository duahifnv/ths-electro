package org.envelope.helperservice.repository;

import jakarta.validation.constraints.NotNull;
import org.envelope.helperservice.entity.WaitingUserRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRequestRepository extends JpaRepository<WaitingUserRequest, Long> {
    Optional<WaitingUserRequest> findFirstByOrderByTimestampDesc();
    void deleteByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    boolean existsByUserId(Long userId);
    List<WaitingUserRequest> findByUserId(Long userId);
}
