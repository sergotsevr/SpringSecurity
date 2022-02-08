package security.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/hello")
    public String hello(){
        return "hello users and admins";
    }

    @PostMapping("/hello")
    public String user(){
        return "hello admin";
    }

    @DeleteMapping("/hello")
    public String admin(){
        return "goodbye admin";
    }
}
