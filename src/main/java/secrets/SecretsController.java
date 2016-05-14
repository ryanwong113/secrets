package secrets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.GroupMembership;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
@RequestMapping("/secrets")
public class SecretsController {

    private static final Logger logger = Logger.getLogger(SecretsController.class);

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
    public String login(@RequestParam("code") String code, Model model) {
        logger.info("[login] Logging in...");

        String longAccessToken = getLongAccessToken(code);

        Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);
        facebook = connection != null ? connection.getApi() : new FacebookTemplate(longAccessToken);

//        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
//        PagedList<Post> feed = facebook.feedOperations().getFeed();
//        model.addAttribute("feed", feed);
        return "redirect:http://localhost:3000/#/facebook-login-redirect/" + longAccessToken;
    }

    private String getLongAccessToken(String code) {
        String accessToken = "";
        try {
            String longLiveAccessTokenUrlString = "https://graph.facebook.com/v2.3/oauth/access_token?"
                + "&client_id=" + environment.getProperty("spring.social.facebook.appId")
                + "&client_secret=" + environment.getProperty("spring.social.facebook.appSecret")
                + "&code=" + code
                + "&redirect_uri=" + URLEncoder.encode("http://192.168.1.82:8080/secrets/login", "UTF-8")
                ;

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

            logger.info("[getLongAccessToken] Access Token: " + accessToken);

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    @RequestMapping("user")
    public @ResponseBody User userProfile() {
        return facebook.userOperations().getUserProfile();
    }

    @RequestMapping(value = "secret-page", method = RequestMethod.GET)
    public @ResponseBody PagedList<GroupMembership> getSecretPages() {

        facebook.restOperations().getForEntity()

        return facebook.groupOperations().getMemberships();
    }

    @RequestMapping(value = "secret-page/{secretPageId}", method = RequestMethod.GET)
    public @ResponseBody Group getSecretPage(@PathVariable("secretPageId") String secretPageId) {
        return facebook.groupOperations().getGroup(secretPageId);
    }

    @RequestMapping(value = "secret-page/{scretPageId}/secret", method = RequestMethod.GET)
    public @ResponseBody String getSecrets(@PathVariable("secretPageId") String secretPageId) {
        return "";
    }

    @RequestMapping(value = "secret-page/{scretPageId}/secret/{secretId}", method = RequestMethod.GET)
    public @ResponseBody String getSecret(@PathVariable("secretPageId") String secretPageId,
                            @PathVariable("secretId") String secretId) {
        return "";
    }

}
