package com.example.demorestapi.common;

import com.example.demorestapi.index.IndexController;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class ErrorsResource extends ResourceSupport {

    @JsonUnwrapped
    private Errors errors;

    public ErrorsResource(Errors errors){
        this.errors = errors;
        add(linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}
