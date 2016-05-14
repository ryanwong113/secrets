package secrets.controller;

import java.util.Date;

import org.apache.log4j.Logger;
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
import secrets.model.Secret;
import secrets.service.SecretService;
import secrets.service.TokenService;

@Controller
@RequestMapping("/secret-page")
public class SecretsController {

    private static final Logger logger = Logger.getLogger(SecretsController.class);

    @Inject
    private SecretService secretService;

    @Inject
    private TokenService tokenService;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(@RequestParam("code") String code) {
        logger.info("[login] Logging in...");

        String longAccessToken = tokenService.getLongAccessToken(code);

//        Connection<Facebook> connection = connectionRepository.findPrimaryConnection(Facebook.class);
//        facebook = connection != null ? connection.getApi() : new FacebookTemplate(longAccessToken);

        return "redirect:http://localhost:3000/#/facebook-login-redirect/" + longAccessToken;
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
