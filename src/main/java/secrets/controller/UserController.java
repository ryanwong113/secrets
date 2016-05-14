package secrets.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;

import javax.inject.Inject;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private Environment environment;
    private Facebook facebook;
    private ConnectionRepository connectionRepository;

    @Inject
    public UserController(Environment environment, Facebook facebook, ConnectionRepository connectionRepository) {
        this.environment = environment;
        this.facebook = facebook;
        this.connectionRepository = connectionRepository;
    }

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public Map<String, String> userProfile(@RequestParam("token") String token) {
        User userProfile = getFacebook(token).userOperations().getUserProfile();

        Map<String, String> userProfileMap = Maps.newHashMap();
        userProfileMap.put("id", userProfile.getId());
        userProfileMap.put("name", userProfile.getName());
        userProfileMap.put("email", userProfile.getEmail());

        return userProfileMap;
    }

    @RequestMapping(value = "/me/page", method = RequestMethod.GET)
    public PagedList<Account> pageAccounts(@RequestParam("token") String token) {
        return getFacebook(token).pageOperations().getAccounts();
    }

    private Facebook getFacebook(String token) {
        return new FacebookTemplate(token);
    }

}
