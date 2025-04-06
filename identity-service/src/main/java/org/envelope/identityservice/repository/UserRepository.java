package org.envelope.identityservice.repository;

import org.envelope.identityservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    int PAGE_SIZE = 5;
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByTag(String tag);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByTag(String tag);
}
