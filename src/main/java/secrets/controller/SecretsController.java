package secrets.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Date;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import secrets.SecretService;
import secrets.model.Secret;

@Controller
@RequestMapping("/secret-page")
public class SecretsController {

    private static final Logger logger = Logger.getLogger(SecretsController.class);

    @Autowired
    private SecretService secretService;

    private Environment environment;
    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    @Inject
    public SecretsController(Environment environment, Facebook facebook, ConnectionRepository connectionRepository) {
        this.environment = environment;
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(@RequestParam("code") String code) {
        logger.info("[login] Logging in...");

        String longAccessToken = getLongAccessToken(code);

//        Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);
//        facebook = connection != null ? connection.getApi() : new FacebookTemplate(longAccessToken);

        return "redirect:http://localhost:3000/#/facebook-login-redirect/" + longAccessToken;
    }

    private String getLongAccessToken(String code) {
        logger.info("[getLongAccessToken] Getting long alive access token with code: " + code);

        String accessToken = "";
        try {
            String longLiveAccessTokenUrlString = "https://graph.facebook.com/v2.3/oauth/access_token?"
                + "&client_id=" + getAppId()
                + "&client_secret=" + getAppSecret()
                + "&code=" + code
                + "&redirect_uri=" + URLEncoder.encode("http://192.168.1.82:8080/secret-page/login", "UTF-8");

            URL longLiveAccessTokenUrl = new URL(longLiveAccessTokenUrlString);
            URLConnection urlConnection = longLiveAccessTokenUrl.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine + "\n");
            }

            String accessTokenObject = stringBuilder.toString();

            JSONObject jsonObject = new JSONObject(accessTokenObject);
            accessToken = (String) jsonObject.get("access_token");

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    private String getAppId() {
        return environment.getProperty("spring.social.facebook.appId");
    }

    private String getAppSecret() {
        return environment.getProperty("spring.social.facebook.appSecret");
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public @ResponseBody
    PagedList<Account> getSecretPages(@RequestParam("token") String token) {
        return getFacebook(token).pageOperations().getAccounts();
    }

    @RequestMapping(value = "/{secretPageId}", method = RequestMethod.GET)
    public @ResponseBody
    Group getSecretPage(@PathVariable("secretPageId") String secretPageId,
                        @RequestParam("token") String token) {
        return getFacebook(token).groupOperations().getGroup(secretPageId);
    }

    @RequestMapping(value = "/{secretPageId}/secret", method = RequestMethod.GET)
    public @ResponseBody
    String getSecrets(@PathVariable("secretPageId") String secretPageId,
                      @RequestParam("token") String token) {
        return "";
    }

    @RequestMapping(value = "/{secretPageId}/secret", method = RequestMethod.POST)
    public void createSecret(@PathVariable("secretPageId") String secretPageId,
                             @RequestParam("token") String token) {
        logger.info("[createSecret] Creating new secret...");
        secretService.addSecret(new Secret("test-title", "test-tag", "test-message", "admin", new Date()));
    }

    @RequestMapping(value = "/{secretPageId}/secret/{secretId}", method = RequestMethod.GET)
    public @ResponseBody
    String getSecret(@PathVariable("secretPageId") String secretPageId,
                     @PathVariable("secretId") String secretId,
                     @RequestParam("token") String token) {
        return "";
    }

    private Facebook getFacebook(String token) {
        return new FacebookTemplate(token);
    }

}
