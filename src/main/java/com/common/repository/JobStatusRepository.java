package com.common.repository;

import com.common.domain.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobStatusRepository extends JpaRepository<JobStatus, Long> {

    String INTSUP_JOB = "intsup_job";

    JobStatus findByApplicationAndJobAndKey(String application, String job, String key);

}
