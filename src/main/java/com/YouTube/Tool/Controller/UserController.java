package com.YouTube.Tool.Controller;

import com.YouTube.Tool.Service.UserService;
import com.YouTube.Tool.Entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! You can now login.");
            return "redirect:/login"; // Registration ke baad login page par bhej do
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // Agar error aaye to register page par hi rakho
        }
    }
}
