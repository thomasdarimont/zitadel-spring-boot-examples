package demo.web;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class UiController {

    @GetMapping("/")
    public String showIndex(Model model) {
        return "index";
    }
}
