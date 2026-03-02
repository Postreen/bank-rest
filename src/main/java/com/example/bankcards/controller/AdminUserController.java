package com.example.bankcards.controller;

import com.example.bankcards.dto.card.admin.AdminUserResponse;
import com.example.bankcards.dto.card.admin.CreateUserRequest;
import com.example.bankcards.dto.card.admin.UpdateUserRequest;
import com.example.bankcards.service.user.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public Page<AdminUserResponse> getUsers(@ParameterObject Pageable pageable) {
        return adminUserService.getUsers(pageable);
    }

    @PostMapping("/create")
    public AdminUserResponse createUser(@RequestBody @Valid CreateUserRequest req) {
        return adminUserService.createUser(req);
    }

    @PatchMapping("/{id}")
    public AdminUserResponse updateUser(@PathVariable long id, @RequestBody UpdateUserRequest req) {
        return adminUserService.updateUser(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        adminUserService.deleteUser(id);
    }
}