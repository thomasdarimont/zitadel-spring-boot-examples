package demo.support;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ZitadelLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {

        var principal = (DefaultOidcUser) auth.getPrincipal();
        var idToken = principal.getIdToken();

        log.info("Propagate logout to zitadel for user. userId={}", idToken.getSubject());

        var issuerUri = idToken.getIssuer().toString();
        var idTokenValue = idToken.getTokenValue();

        var defaultRedirectUri = generateAppUri(request);

        var logoutUrl = createZitdaelLogoutUrl(idTokenValue, defaultRedirectUri);

        try {
            response.sendRedirect(logoutUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String generateAppUri(HttpServletRequest request) {
        var hostname = request.getServerName() + ":" + request.getServerPort();
        var isStandardHttps = "https".equals(request.getScheme()) && request.getServerPort() == 443;
        var isStandardHttp = "http".equals(request.getScheme()) && request.getServerPort() == 80;
        if (isStandardHttps || isStandardHttp) {
            hostname = request.getServerName();
        }
        return request.getScheme() + "://" + hostname + request.getContextPath();
    }

    private String createZitdaelLogoutUrl(String idTokenValue, String postRedirectUri) {
        return "https://accounts.zitadel.ch/oauth/v2/endsession?id_token_hint=" + idTokenValue + "&post_logout_redirect_uri=" + postRedirectUri;
    }
}
