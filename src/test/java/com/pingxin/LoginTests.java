package com.pingxin;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.pingxin.config.WebSecurityConfig;
import com.pingxin.model.ApplicationUser;
import com.pingxin.service.ApplicationUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
class LoginTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WebSecurityConfig webSecurityConfig;

    @MockBean
    private ApplicationUserService applicationUserService;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void shouldReturnIfCredentialsCorrect() throws Exception {
        ApplicationUser applicationUser = new ApplicationUser("test", "test");
        this.mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(applicationUser.toJson()))
                .andExpect(status().isOk());
    }

}
