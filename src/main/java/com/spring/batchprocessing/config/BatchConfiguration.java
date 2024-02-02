package com.spring.batchprocessing.config;

import com.spring.batchprocessing.listener.PersonJobCompletionNotificationListener;
import com.spring.batchprocessing.model.Person;
import com.spring.batchprocessing.processor.PersonProcessor;
import com.spring.batchprocessing.resource.PersonResource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    @Bean
    public FlatFileItemReader<PersonResource> reader() {
        return new FlatFileItemReaderBuilder<PersonResource>()
                .name("personReader")
                .resource(new ClassPathResource("person-data.csv"))
                .delimited()
                .names("firstname", "lastname", "dob")
                .targetType(PersonResource.class)
                .build();
    }

    @Bean
    public PersonProcessor processor() {
        return new PersonProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Person>()
                .sql("INSERT INTO people (first_name, last_name, age) VALUES (:firstname, :lastname, :age)")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public Job importUserJob(JobRepository jobRepository, Step step, PersonJobCompletionNotificationListener listener) {
        return new JobBuilder("PersonJob", jobRepository)
                .listener(listener)
                .start(step)
                .build();
    }

    @Bean
    public Step step(JobRepository jobRepository, DataSourceTransactionManager transactionManager,
                     FlatFileItemReader<PersonResource> reader, PersonProcessor processor,
                     JdbcBatchItemWriter<Person> writer) {
        return new StepBuilder("First Step", jobRepository)
                .<PersonResource, Person> chunk(4, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
