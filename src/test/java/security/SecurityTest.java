package security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import security.model.AuthRequest;
import security.sercive.JwtTokenProvider;

import javax.servlet.Filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.util.AssertionErrors.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@ActiveProfiles(value = "authtest")
@WebAppConfiguration
public class SecurityTest {

    private MockMvc mock;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private Filter springSecurityFilterChain;

    private final String INCORRECT_NAME = "name";
    private final String INCORRECT_PWD = "pwd";

    private final String USER_NAME = "user";
    private final String USER_PWD = "user";
    private final String ADMIN_NAME = "admin";
    private final String ADMIN_PWD = "admin";


    @BeforeEach
    public void setup() {
        this.mock = MockMvcBuilders.webAppContextSetup(this.wac).apply(springSecurity(springSecurityFilterChain)).build();
    }

    @DisplayName("Логин")
    @ParameterizedTest
    @CsvSource({USER_NAME + "," + USER_PWD + "," + "/login" + "," + "false",
            INCORRECT_NAME + "," + INCORRECT_PWD + "," + "/login" + "," + "true",
            ADMIN_NAME + "," + ADMIN_PWD + "," + "/login" + "," + "false"})
    void loginTest(String login, String password, String url, Boolean is403) throws Exception {

        postTestAuthentication(url, is403, new AuthRequest(login, password).toString());
    }


    @DisplayName("Тест метода для пользователей")
    @ParameterizedTest
    @CsvSource({USER_NAME + "," + USER_PWD + "," + "/hello" + "," + "false",
            ADMIN_NAME + "," + ADMIN_PWD + "," + "/hello" + "," + "false"})
    void roleTest(String login, String password, String url, Boolean is403) throws Exception {

        getTest(login, password, url, is403, prepareToken(login, password));
    }

    @DisplayName("Тест метода для админов")
    @ParameterizedTest
    @CsvSource({USER_NAME + "," + USER_PWD + "," + "/hello" + "," + "true",
            ADMIN_NAME + "," + ADMIN_PWD + "," + "/hello" + "," + "false"})
    void roleAdminTest(String login, String password, String url, Boolean is403) throws Exception {

        postTest(url, is403, prepareToken(login, password));
    }

    String prepareToken (String login, String password){
        return jwtTokenProvider.createToken(login, password);
    }

    public void getTest(String login, String password, String url, Boolean is403, String token) throws Exception {
        mock.perform(get(url)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(httpBasic(login, password)))
                .andDo(print())
                .andExpect(result -> {
                    if (is403) {
                        assertEquals(403, result.getResponse().getStatus());
                    } else {
                        assertNotEquals("", 403, result.getResponse().getStatus());
                    }
                });
    }

    public void postTestAuthentication(String url, Boolean is401, String content) throws Exception {

        mock.perform(post(url)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> {
                    if (is401) {
                        assertEquals(401, result.getResponse().getStatus());
                    } else {
                        assertEquals(200, result.getResponse().getStatus());
                    }
                });

    }
    public void postTest(String url, Boolean is403, String token) throws Exception {

        mock.perform(post(url)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(result -> {
                    if (is403) {
                        assertEquals(403, result.getResponse().getStatus());
                    } else {
                        assertEquals(200, result.getResponse().getStatus());
                    }
                });

    }
}
