package se.survivor.net.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sayHello")
public class HelloController {

    @GetMapping("{name}")
    public String test(@PathVariable("name") String name) {
        return "Hello" + name;
    }
}
