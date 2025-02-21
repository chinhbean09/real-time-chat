package com.chinhbean.realtimechat.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @RequestMapping("/")
    public String index(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("username");
        if (username == null || username.isEmpty()) {
            return "redirect:/login";
        }
        //Nếu đã đăng nhập, lấy username từ session, đưa vào model, và hiển thị trang chat.html.
        model.addAttribute("username", username);

        return "chat";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, @RequestParam(defaultValue = "") String username) {
        username = username.trim();

        if (username.isEmpty()) {
            return "login";
        }
        //Nếu hợp lệ → lưu username vào session và chuyển hướng đến /.
        request.getSession().setAttribute("username", username);

        return "redirect:/";
    }

    @RequestMapping(path = "/logout")
    public String logout(HttpServletRequest request) {

        /*
        * Xóa session, đăng xuất người dùng.
        Chuyển hướng về trang đăng nhập /login.
        */
        request.getSession(true).invalidate();

        return "redirect:/login";
    }



}
