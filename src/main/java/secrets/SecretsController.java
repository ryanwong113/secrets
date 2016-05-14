package secrets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.core.env.Environment;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Group;
import org.springframework.social.facebook.api.GroupMembership;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;

@Controller
@RequestMapping("/secrets")
public class SecretsController {

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
    public String login(Model model) {
        String accessToken = "EAACEdEose0cBALBF4v9tBTPbJLSIPQnNzIBpuDooeFHPxZAGzqushxEOyRZAvHZCzIOT9phDhnmruJbulb01P6GncmfuO0gEWZBYTIMZB5OaX4bbP1TK2yKpX6GZCpCZBZAvJ1J8TiBJ8B7m5monM0MxfTzLpXcFK8lWFwu1daE9ZBIs4n78l3sbn2yrJJhZANPoy6vlnrCZBBQqQZDZD";

        String longAccessToken = getLongAccessToken(accessToken);

        Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);
        facebook = connection != null ? connection.getApi() : new FacebookTemplate(longAccessToken);

        model.addAttribute("facebookProfile", facebook.userOperations().getUserProfile());
        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);
        return "home";
    }

    private String getLongAccessToken(String accessToken) {
        try {
            String longLiveAccessTokenUrlString = "https://graph.facebook.com/oauth/access_token?"
                + "grant_type=client_credentials"
                + "&client_id=" + environment.getProperty("spring.social.facebook.appId")
                + "&client_secret=" + environment.getProperty("spring.social.facebook.appSecret")
                + "&code=" + accessToken
//                + "&redirect_uri=http://localhost:8080/"
                ;

            URL longLiveAccessTokenUrl = new URL(longLiveAccessTokenUrlString);
            URLConnection urlConnection = longLiveAccessTokenUrl.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine + "\n");
            }

            accessToken = stringBuilder.toString();
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
