package com.cobin.hackok.domain.member.controller;

import com.cobin.hackok.domain.member.dto.Member;
import com.cobin.hackok.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
public class MemberLoginController {

    private final MemberService service;

    @Autowired
    public MemberLoginController(MemberService service) {
        this.service = service;
    }
    //1. 로그인

    // 1-1. 로그인 폼
    @GetMapping("/login")
    public String loginForm(){
        return "login/loginForm";
    }

    //1-2. 로그인 실행
    @PostMapping("/login")
    public String doLogin(@ModelAttribute Member member, HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL){
        Member loginResult = service.login(member);
        if (loginResult == null){ // 로그인에 실패한 경우
           return "login/loginForm";
        }
        // 로그인이 성공한 경우
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", loginResult);
        log.info("로그인 성공");

        return "redirect:" + redirectURL;
    }

    //2. 회원가입

    // 2-1. 회원가입 폼
    @GetMapping("/signup")
    public String signupForm(){
        return "login/signupForm";
    }

    //2-2. 회원가입 실행
    @PostMapping("/signup")
    public String doSignup(Member member){
        boolean doSignup = service.signup(member);
        if(doSignup) log.info("회원가입 완료");

        return "redirect:login";
    }


    //3. 비밀번호 찾기

    //3-1. 비밀번호 찾기 폼
    @GetMapping("/forgotpassword")
    public String forgotPasswordForm(){
        return "login/forgotPasswordForm";
    }

    //3-2. 비밀번호 찾기 실행
    @PostMapping("/forgotpassword")
    public String findPassword(@RequestParam(name = "loginId") String loginId, @RequestParam(name = "name") String name, Model model){
        Optional<Member> findMember = service.findPasswordByLoginIdAndName(loginId, name);
        if(findMember.isPresent()) model.addAttribute("password", findMember.get().getPassword());
        return "login/noticePasswordForm";
    }


    //4. 로그아웃
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();  // 세션 무효화
        return "redirect:/login";
    }

    //5. 마이페이지

    //5-1. 마이페이지 read
    @GetMapping("/mypage")
    public String readMyInfo(HttpSession session, Model model){
        Member member = (Member) session.getAttribute("loginMember");
        String loginId = member.getLoginId();

        Optional<Member> memberInfo = service.readMyInfo(loginId);
        memberInfo.ifPresent(value -> model.addAttribute("member", value));
        return "myPage/myInfoPage";
    }

    //5-2-1. 비밀번호 updateForm
    @GetMapping("/changePasswordForm")
    public String changePasswordForm(HttpSession session, Model model){
        Member member = (Member) session.getAttribute("loginMember");
        String loginId = member.getLoginId();

        Optional<Member> memberInfo = service.readMyInfo(loginId);
        memberInfo.ifPresent(value -> model.addAttribute("member", value));
        return "myPage/changePasswordForm";
    }

    //5-2-2. 비밀번호 update
    @PostMapping("/changePassword")
    public String changePassword(HttpSession session, @RequestParam(name = "oldPassword") String oldPassword,
                                 @RequestParam(name = "newPassword") String newPassword, Model model){
        Member member = (Member) session.getAttribute("loginMember");
        String loginId = member.getLoginId();

        Optional<Member> memberInfo = service.readMyInfo(loginId);
        if(memberInfo.isPresent()){
            Member newMember = memberInfo.get();
            newMember.setPassword(newPassword);
            service.changePassword(newMember);
        }
        return "redirect:/mypage";
    }

    //5-2-3. 현재 비밀번호 확인
    @PostMapping("/confirmPassword")
    @ResponseBody
    public int confirmPassword(HttpSession session, @RequestParam(name = "oldPassword") String oldPassword, Model model){
        Member member = (Member) session.getAttribute("loginMember");
        log.info("html비번 " +oldPassword);
        log.info(member.getPassword());
        if (member.getPassword().equals(oldPassword)) return 1;
        else return 0;
    }


    //5-3-1. 회원정보 updateForm
    @GetMapping("/changeInfoForm")
    public String changeInfoForm(HttpSession session, Model model){
        Member member = (Member) session.getAttribute("loginMember");
        String loginId = member.getLoginId();

        Optional<Member> memberInfo = service.readMyInfo(loginId);
        memberInfo.ifPresent(value -> model.addAttribute("member", value));
        return "myPage/changeInfoForm";
    }

    //5-3-2. 회원정보 update
    @PostMapping("/changeInfo")
    public String changeInfo(HttpSession session, @RequestParam(name = "name") String name, @RequestParam(name = "birthday") String birthday,
                             @RequestParam(name = "mobileNumber") String mobileNumber, @RequestParam(name = "gender") String gender,
                             @RequestParam(name = "address") String address, Model model){
        Member member = (Member) session.getAttribute("loginMember");
        String loginId = member.getLoginId();

        Optional<Member> memberInfo = service.readMyInfo(loginId);
        if(memberInfo.isPresent()){
            Member newMember = memberInfo.get();
            newMember.setName(name);
            newMember.setBirthday(birthday);
            newMember.setGender(gender);
            service.changeMemberInfo(newMember);
        }
        return "myPage/myInfoPage";
    }

    //6. 회원 탈퇴 delete
    @PostMapping("/myPage/deleteaccount")
    public String deleteAccountWithMember(HttpServletRequest request){
        Member member = (Member) request.getSession().getAttribute("loginMember");
        request.getSession().invalidate();  // 세션 무효화
        service.deleteAccount(member);
        return "redirect:/login";
    }



}
