package roarbits.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // 현재 인증된 사용자 정보 가져오기
import org.springframework.web.bind.annotation.*;

import roarbits.global.api.ApiResponse;
import roarbits.global.api.SuccessCode;

import roarbits.user.dto.UserResponse;
import roarbits.user.entity.User;
import roarbits.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getUserInfo(@AuthenticationPrincipal User user) {
        UserResponse userResponse = userService.getUserInfo(user.getUsername());
        return ApiResponse.onSuccess(SuccessCode.USER_INFO_GET_SUCCESS, userResponse);
    }

    @DeleteMapping("/me")
    public ApiResponse<String> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getUsername());
        return ApiResponse.onSuccess(SuccessCode.USER_DELETE_SUCCESS, "계정이 삭제되었습니다.");
    }

}
