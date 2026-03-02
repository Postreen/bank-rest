package bankcards.security;

import com.example.bankcards.entity.UserEntity;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = com.example.bankcards.BankRestApplication.class)
@AutoConfigureMockMvc
class SecuritySmokeTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenService tokenService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("1234"));
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        admin = userRepository.save(admin);

        UserEntity user = new UserEntity();
        user.setUsername("user");
        user.setPasswordHash(passwordEncoder.encode("1234"));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user = userRepository.save(user);

        adminToken = tokenService.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
        userToken = tokenService.generateToken(user.getId(), user.getUsername(), user.getRole());
    }

    @Test
    void admin_cards_without_token_returns_401() throws Exception {
        mockMvc.perform(get("/admin/cards"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void admin_cards_with_user_token_returns_403() throws Exception {
        mockMvc.perform(get("/admin/cards")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }
    @Test
    void admin_cards_with_admin_token_returns_200() throws Exception {
        mockMvc.perform(get("/admin/cards")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk());
    }
}
