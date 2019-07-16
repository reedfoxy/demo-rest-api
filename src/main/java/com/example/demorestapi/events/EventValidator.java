package com.example.demorestapi.events;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidator {

    public Errors validate(EventDto eventDto, Errors errors){

        // 필드에 값이 없는 경우
        if ( errors.hasErrors() ){
            return errors;
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

        return errors;
    }

}
