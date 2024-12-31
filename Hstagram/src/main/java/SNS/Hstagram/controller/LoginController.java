package SNS.Hstagram.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginForm() {
        // templates/login.html 파일 이름("login")을 리턴
        return "login";
    }
}
