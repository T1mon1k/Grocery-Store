package com.example.onlinestore.controller;

import com.example.onlinestore.dto.UserDto;
import com.example.onlinestore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistration(Model m) {
        m.addAttribute("userDto", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userDto") UserDto dto,
            BindingResult br,
            Model m
    ) {
        if (br.hasErrors()) {
            return "register";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            br.rejectValue("confirmPassword", "password.no_match", "Паролі не співпадають");
            return "register";
        }
        try {
            userService.registerNew(dto);
        } catch (RuntimeException ex) {
            m.addAttribute("error", ex.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
