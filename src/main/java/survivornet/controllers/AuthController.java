package survivornet.controllers;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import survivornet.exceptions.InvalidIdException;
import survivornet.exceptions.InvalidRequestParamsException;
import survivornet.exceptions.UnauthorizedException;
import survivornet.models.User;
import survivornet.services.db.UserDbService;
import survivornet.utils.JWTUtility;
import survivornet.utils.Secret;
import survivornet.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final UserDbService userDbService;

    public AuthController(UserDbService userDbService) {
        this.userDbService = userDbService;
    }

    @PostMapping("oath/github")
    public Map<String, String> oauthWithGithub(@RequestParam("code") String code) throws IOException, ParseException {

        String githubUserToken = getUserTokenFromGithub(code);
        JsonObject userInfo = getUserInfoFromGithub(githubUserToken);
        String username = userInfo.get("login").getAsString();
        String email = userInfo.get("email").getAsString();
        try {
            User user = userDbService.getUserByEmail(email);
        } catch (InvalidIdException e) { // new User
            userDbService.addUser(username, username, null, email, null, "");
        }
        return Map.of(Constants.STATUS, Constants.SUCCESS,
                Constants.AUTHORIZATION, JWTUtility.generateToken(username),
                Constants.USERNAME, username);
    }

    @GetMapping("logout")
    public Map<String, String> logout(@RequestHeader(Constants.AUTHORIZATION) String authToken) {
        return Map.of(Constants.STATUS, Constants.SUCCESS);
    }

    @PostMapping("login")
    public Map<String, String> login(
            @RequestBody Map<String, String> body) throws UnauthorizedException {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            throw new UnauthorizedException("Empty username or password");
        }
        if (userDbService.authenticateByPassword(username, password)) {
            var authToken = JWTUtility.generateToken(username);
            return Map.of(Constants.AUTHORIZATION, authToken,
                    Constants.STATUS, Constants.SUCCESS);
        }
        throw new UnauthorizedException("Username or password was wrong");
    }

    @PostMapping("register")
    public Map<String, String> register(@RequestBody Map<String, String> body) throws InvalidRequestParamsException {
        try {
            String username = Objects.requireNonNull(body.get(Constants.USERNAME));
            String password = Objects.requireNonNull(body.get(Constants.PASSWORD));
            String name = Objects.requireNonNull(body.get(Constants.NAME));
            String email = Objects.requireNonNull(body.get(Constants.EMAIL));
            Date birthDate = Date.valueOf(body.get(Constants.BIRTHDATE));

            userDbService.addUser(username, name, password, email, birthDate, "");
            var authToken = JWTUtility.generateToken(username);
            return Map.of(Constants.STATUS, Constants.SUCCESS,
                    Constants.AUTHORIZATION, authToken);
        } catch (NullPointerException e) {
            throw new InvalidRequestParamsException("All the Fields are required");
        } catch (InvalidIdException e) {
            throw new InvalidIdException("User already exists");
        }
    }

    //
    private String getUserTokenFromGithub(String code) throws IOException {
        return getResource("https://github.com/login/oauth/access_token?client_id="
                + Secret.clientId + "&client_secret=" + Secret.clientSecret + "&code=" + code, Map.of("Accept", "application/vnd.github+json"))
                .get("access_token")
                .getAsString();
    }

    private JsonObject getUserInfoFromGithub(String githubUserToken) throws IOException {
        return getResource("https://api.github.com/user", Map.of("Accept", "application/vnd.github+json",
                "Authorization", "Bearer " + githubUserToken,
                "X-GitHub-Api-Version", "2022-11-28"));
    }

    private JsonObject getResource(@NotNull String url, Map<String, String> headers) throws IOException {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        for (var entry :
                headers.entrySet()) {
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }
        int responseCode = con.getResponseCode();
        if (responseCode != 200)
            throw new IOException("foreign api sent a response with status code " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        return new Gson().fromJson(sb.toString(), JsonObject.class);
    }

}
