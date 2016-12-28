package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class ViewController {

    @Autowired
    private SignupRepository signupRepository;

    @GetMapping("/")
    public String defaultMapping() {
        return "form";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

}
