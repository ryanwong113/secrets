package secrets.controller;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.social.facebook.api.Account;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.Page;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;

import javax.inject.Inject;
import secrets.service.TokenService;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    @Inject
    private TokenService tokenService;

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

    @RequestMapping(value = "/me/page/{pageId}", method = RequestMethod.GET)
    public Page pageAccount(@PathVariable("pageId") String pageId,
                            @RequestParam("token") String token) {

        String pageAccessToken = tokenService.getPageAccessToken(pageId, token);

//        ResponseEntity<String> responseEntity = getFacebook(token).restOperations().getForEntity(String.format("/%s/picture", pageId), String.class);
//        logger.info(responseEntity.getBody());
        return getFacebook(pageAccessToken).pageOperations().getPage(pageId);
    }

    private Facebook getFacebook(String token) {
        return new FacebookTemplate(token);
    }

}
