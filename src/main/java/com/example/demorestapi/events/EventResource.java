package com.example.demorestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Getter
public class EventResource extends ResourceSupport {

    @JsonUnwrapped
    private Event event;

    public EventResource(Event event){
        this.event = event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

}
