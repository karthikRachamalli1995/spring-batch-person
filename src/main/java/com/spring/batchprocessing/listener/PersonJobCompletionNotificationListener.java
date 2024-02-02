package com.spring.batchprocessing.listener;

import com.spring.batchprocessing.model.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PersonJobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(PersonJobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    public PersonJobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus().equals(BatchStatus.COMPLETED)) {
            logger.info("Spring Batch JOB FINISHED!");

            jdbcTemplate.query("SELECT first_name as firstname, last_name as lastname, age FROM people",
                            new DataClassRowMapper<>(Person.class))
                    .forEach(person -> logger.info("Found <{{}}> in the database.", person));
        }
    }
}
