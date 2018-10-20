package controller;

import entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    //需要先得到登录界面
    @RequestMapping("/preLogin")
    public ModelAndView preLogin(){
        return new ModelAndView("login");
    }

    @RequestMapping("/login")
    public ModelAndView login(User user){
        ModelAndView modelAndView = new ModelAndView("main");
        //得到 Subject
        Subject subject = SecurityUtils.getSubject();
        //获取 token，用于验证
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(user.getUsername(), user.getPassword());
        try {
            //尝试登录
            subject.login(usernamePasswordToken);
            modelAndView.addObject("username", user.getUsername());
        } catch (AuthenticationException e){
            modelAndView = new ModelAndView("login");
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
    }

    @RequestMapping("testAuthorize")
    public String testAuthorize(){
        return "testAuthorize";
    }

    @RequestMapping("unAuthorize")
    public String unAuthorize(){
        return "unAuthorize";
    }
}
