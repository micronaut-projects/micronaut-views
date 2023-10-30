package com.projectcheckins.users.repositories.jdbc;

import com.projectcheckins.users.services.PasswordEncoder;
import com.projectcheckins.users.services.UserSave;
import com.projectcheckins.users.services.UserService;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Singleton
public class UserServiceJdbc implements UserService {

    private final RoleJdbcRepository roleJdbcRepository;
    private final UserJdbcRepository userJdbcRepository;
    private final UserRoleJdbcRepository userRoleJdbcRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceJdbc(RoleJdbcRepository roleJdbcRepository,
                           UserJdbcRepository userJdbcRepository,
                           PasswordEncoder passwordEncoder,
                           UserRoleJdbcRepository userRoleJdbcRepository) {
        this.roleJdbcRepository = roleJdbcRepository;
        this.userJdbcRepository = userJdbcRepository;
        this.userRoleJdbcRepository = userRoleJdbcRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(UserSave userSave, List<String> authorities) {
        register(userSave.email(), userSave.username(), userSave.password(), authorities);
    }

    @Transactional
    public void register(@Email String email,
                         @NotBlank String username,
                         @NotBlank String rawPassword,
                         @Nullable List<String> authorities) {

        UserEntity user = userJdbcRepository.findByUsername(username).orElse(null);
        if (user == null) {
            final String encodedPassword = passwordEncoder.encode(rawPassword);
            user = userJdbcRepository.save(new UserEntity(null, email, username, encodedPassword, true, false,  false, false));
        }

        if (user != null && authorities != null) {
            for (String authority : authorities) {
                RoleEntity role = roleJdbcRepository.findByAuthority(authority).orElseGet(() -> roleJdbcRepository.save(authority));
                UserRoleId userRoleId = new UserRoleId(user.id(), role.id());
                if (userRoleJdbcRepository.findById(userRoleId).isEmpty()) {
                    userRoleJdbcRepository.save(new UserRoleEntity(userRoleId));
                }
            }
        }
    }
}
