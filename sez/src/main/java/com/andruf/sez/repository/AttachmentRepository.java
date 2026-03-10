package com.andruf.sez.repository;

import com.andruf.sez.entity.Attachment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AttachmentRepository extends IRepository<Attachment, UUID>{
    @Query("SELECT a.fileName FROM Attachment a " +
            "JOIN a.assignment asgn " +
            "JOIN asgn.lesson l " +
            "WHERE l.endTime < :aMonthAgo AND l.status = 'FINISHED'")
    List<String> findFileNamesByOldLessons(@Param("aMonthAgo") OffsetDateTime aMonthAgo);
}
