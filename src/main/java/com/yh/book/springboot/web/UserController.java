package com.yh.book.springboot.web;

import com.yh.book.springboot.config.auth.LoginUser;
import com.yh.book.springboot.config.auth.dto.UserDto;
import com.yh.book.springboot.config.auth.validator.CheckEmailValidator;
import com.yh.book.springboot.config.auth.validator.CheckNicknameValidator;
import com.yh.book.springboot.config.auth.validator.CheckUsernameValidator;
import com.yh.book.springboot.service.posts.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * 회원 관련 Controller
 */
@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    private final CheckUsernameValidator checkUsernameValidator;
    private final CheckNicknameValidator checkNicknameValidator;
    private final CheckEmailValidator checkEmailValidator;
    private final AuthenticationManager authenticationManager;

    /* 커스텀 유효성 검증을 위해 추가 */
    @InitBinder
    public void validatorBinder(WebDataBinder binder) {
        binder.addValidators(checkUsernameValidator);
        binder.addValidators(checkNicknameValidator);
        binder.addValidators(checkEmailValidator);
    }

    @GetMapping("/auth/join")
    public String join() {
        return "user/user-join";
    }

    /* 회원가입 */
    @PostMapping("/auth/joinProc")
    public String joinProc(@Valid UserDto.Request dto, Errors errors, Model model) {
        if (errors.hasErrors()) {
             /* 회원가입 실패시 입력 데이터 값을 유지 */
            model.addAttribute("userDto", dto);

            /* 유효성 통과 못한 필드와 메시지를 핸들링 */
            Map<String, String> validatorResult = userService.validateHandling(errors);
            for (String key : validatorResult.keySet()) {
                model.addAttribute(key, validatorResult.get(key));
            }
            /* 회원가입 페이지로 다시 리턴 */
            return "user/user-join";
        }
        userService.userJoin(dto);
        return "redirect:/auth/login";
    }

    @GetMapping("/auth/login")
    public String login(@RequestParam(value = "error", required = false)String error,
                        @RequestParam(value = "exception", required = false)String exception,
                        Model model) {
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "user/user-login";
    }

    /* Security에서 로그아웃은 기본적으로 POST지만, GET으로 우회 */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/";
    }

    /* 회원정보 수정 */
    @GetMapping("/modify")
    public String modify(@LoginUser UserDto.Response user, Model model) {
        if (user != null) {
            model.addAttribute("user", user);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("login_Info", user.getLogin_Info());
        }
        return "user/user-modify";
    }
    /* 회원정보 수정 */
    @PutMapping("/api/user")
    public ResponseEntity<String> updateUser(@RequestBody UserDto.Request dto) {
        try {
            userService.modify(dto);

            /* 변경된 세션 등록 */
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok("회원 수정이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류가 발생했습니다.");
        }
    }
}
