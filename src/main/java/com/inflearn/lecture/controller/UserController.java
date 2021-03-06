package com.inflearn.lecture.controller;

import com.inflearn.lecture.exception.UserNotFoundException;
import com.inflearn.lecture.user.User;
import com.inflearn.lecture.user.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/v1")
public class UserController {
    @Autowired
    private UserDaoService service;

    @GetMapping("/users")
    public List<User> findAllUsers(){
        return service.findAll();
    }

    //hateos 설정
    //springBoot 2.1 -> Resource, ControllerLinkBuilder
    //springBoot 2.2 -> EntityModel, WebMvcLinkBuilder
    @GetMapping("/users/{id}")
    public EntityModel<User> findById(@PathVariable int id){
        User user = service.findById(id);
        if(user == null){
            throw new UserNotFoundException(String.format("ID[%s] Not Found",id));
        }

        //hateos 관련 코드
        EntityModel<User> model = new EntityModel<>(user);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).findAllUsers());
        model.add(linkTo.withRel("all-users"));
        return model;
    }

    @PostMapping("/user")
    public ResponseEntity<User> save(@Valid @RequestBody User user){
        User saveUser = service.save(user);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(saveUser.getId())
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable int id){
    User user = service.deleteById(id);
        if(user == null){
            throw new UserNotFoundException(String.format("ID[%s] Not Found",id));
        }
    }
}
