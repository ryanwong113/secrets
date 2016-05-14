package secrets.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class TokenService {

    private static final Logger logger = Logger.getLogger(TokenService.class);

    @Inject
    private Environment environment;

    public String getLongAccessToken(String code) {
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

    public String getPageAccessToken(String pageId, String accessToken) {
        logger.info("[getPageAccessToken] Getting page access token with pageId: " + pageId);

        String pageAccessToken = "";
        try {
            String pageAccessTokenUrlString = String.format("https://graph.facebook.com/v2.3/%s?fields=%s", pageId, accessToken);

            URL pageAccessTokenUrl = new URL(pageAccessTokenUrlString);
            URLConnection urlConnection = pageAccessTokenUrl.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder stringBuilder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine + "\n");
            }

            String pageAccessTokenObject = stringBuilder.toString();

            JSONObject jsonObject = new JSONObject(pageAccessTokenObject);
            pageAccessToken = (String) jsonObject.get("access_token");

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageAccessToken;
    }

}
