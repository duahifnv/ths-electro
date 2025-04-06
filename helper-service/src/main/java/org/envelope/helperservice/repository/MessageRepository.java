package org.envelope.helperservice.repository;

import jakarta.validation.constraints.NotNull;
import org.envelope.helperservice.entity.Helper;
import org.envelope.helperservice.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByTimestampAfterAndUserIdAndHelperAndMessageTo(Timestamp timestamp, Long userId, Helper helper, String messageTo);
}
