package com.example.demorestapi.events;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EventValidator {

    public Optional<ResponseEntity> validate(EventDto eventDto, Errors errors){

        if ( errors.hasErrors() ){
            return Optional.of(ResponseEntity.badRequest().body(errors));
        }

        if((eventDto.getBasePrice() > eventDto.getMaxPrice()) && eventDto.getMaxPrice() != 0 ){
            errors.rejectValue("basePrice","wrongValue", "BasePrice is wrong.");
            errors.rejectValue("maxPrice","wrongValue", "maxPrice is wrong.");
            errors.reject("wrongPrices","Values of Prices are wrong");
        }

        LocalDateTime endEventDateTime = eventDto.getEndEventDateTime();
        if (endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getCloseEnrollmentDateTime()) ||
        endEventDateTime.isBefore(eventDto.getBeginEnrollmentDateTime())) {
            errors.rejectValue("endEventDateTime", "wrongValue", "endEventDateTime is wrong.");
        }
        // TODO beginEventDateTime
        if ( errors.hasErrors() ){
            return Optional.of(ResponseEntity.badRequest().body(errors));
        }
        return Optional.empty();
    }
}
