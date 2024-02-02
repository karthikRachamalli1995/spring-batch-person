package com.spring.batchprocessing.processor;

import com.spring.batchprocessing.model.Person;
import com.spring.batchprocessing.resource.PersonResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PersonProcessor implements ItemProcessor<PersonResource, Person> {

    private static final Logger logger = LoggerFactory.getLogger(PersonProcessor.class);

    @Override
    public Person process(PersonResource resource) {
        return Person.builder()
                .firstname(resource.getFirstname())
                .lastname(resource.getLastname())
                .age(calculateAge(resource))
                .build();
    }

    private Integer calculateAge(PersonResource resource) {
        try {
            DateFormat dobDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date dob_date_format = dobDateFormat.parse(resource.getDob());
            Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setTime(dob_date_format);
            return Year.now().getValue() - calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            logger.error("Date of Birth of person with first name: {} and lastname: {} is not \"dd-MM-yyyy\" format",
                    resource.getFirstname(), resource.getLastname());
        }
        return null;
    }
}
