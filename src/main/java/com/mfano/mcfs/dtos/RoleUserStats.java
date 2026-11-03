package com.mfano.mcfs.dtos;

import java.util.List;

import com.mfano.mcfs.auth.models.Role;
import com.mfano.mcfs.auth.models.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RoleUserStats {

    public RoleUserStats(Role role, List<User> users, long userCount) {
        this.role = role;
        this.users = users;
        this.userCount = userCount;
    }
    private Role role;
    private List<User> users;
    private long userCount;
    private double percentage;
}
