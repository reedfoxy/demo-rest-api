package com.example.demorestapi.events;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@Getter
public class EventResource extends ResourceSupport {

    // jsonUnWrapped 하게되면 event : {} 형태가 아닌 원본 데이터 자체를 json 에 삽입한다.
    @JsonUnwrapped
    private Event event;

    public EventResource(Event event){
        this.event = event;
        add(linkTo(EventController.class).slash(event.getId()).withSelfRel());
    }

}
