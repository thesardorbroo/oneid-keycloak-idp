package uz.sardorbroo.keycloak.authenticator;

import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.provider.AuthenticationRequest;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.services.Urls;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class OneIdProvider extends AbstractOAuth2IdentityProvider<OneIdProviderConfig> {

    private final static String OAUTH2_SCOPE_PARAMETER = "scope";
    private final static String OAUTH2_STATE_PARAMETER = "state";
    private final static String OAUTH2_REDIRECT_URI_PARAMETER = "redirect_uri";
    private final static String OAUTH2_RESPONSE_TYPE_PARAMETER = "response_type";
    private final static String OAUTH2_ONE_CODE = "one_code";
    private final static String OAUTH2_CODE = "code";
    private final static String OAUTH2_CLIENT_ID_PARAMETER = "client_id";
    private final static String OAUTH2_CLIENT_SECRET_PARAMETER = "client_secret";
    private final static String OAUTH2_GRANT_TYPE_PARAMETER = "grant_type";
    private final static String OAUTH2_AUTHORIZATION_CODE = "one_authorization_code";
    private static final String OAUTH2_ACCESS_TOKEN_PARAMETER = "access_token";
    private static final String OAUTH2_ACCESS_TOKEN_IDENTIFY = "one_access_token_identify";

    public OneIdProvider(KeycloakSession session, OneIdProviderConfig config) {
        super(session, config);
    }

    @Override
    public void close() {

    }

    @Override
    protected String getDefaultScopes() {
        return getConfig().getDefaultScope();
    }

    @Override
    public Response performLogin(AuthenticationRequest request) {

        logger.debug("Start requesting to get authorization code");

        try {
            URI authorizationUrl = this.createAuthorizationUrl(request).build();

            return Response.seeOther(authorizationUrl).build();
        } catch (Exception e) {
            throw new IdentityBrokerException("Could not create authentication request.", e);
        }
    }

    @Override
    protected UriBuilder createAuthorizationUrl(AuthenticationRequest request) {
        logger.debug("Start creating authorization URL for getting temporary code");

        final UriBuilder uri = UriBuilder.fromUri(getConfig().getAuthorizationUrl())
                .queryParam(OAUTH2_RESPONSE_TYPE_PARAMETER, OAUTH2_ONE_CODE)
                .queryParam(OAUTH2_REDIRECT_URI_PARAMETER, request.getRedirectUri())
                .queryParam(OAUTH2_CLIENT_ID_PARAMETER, getConfig().getClientId())
                .queryParam(OAUTH2_SCOPE_PARAMETER, getConfig().getDefaultScope())
                .queryParam(OAUTH2_STATE_PARAMETER, request.getState().getEncoded());

        logger.debug("Authorization URL is created. URL: " + uri.toTemplate());
        return uri;
    }

    @Override
    public SimpleHttp authenticateTokenRequest(SimpleHttp tokenRequest) {
        return super.authenticateTokenRequest(tokenRequest);
    }

    @Override
    protected SimpleHttp buildUserInfoRequest(String subjectToken, String userInfoUrl) {
        logger.debug("Start fetching user info from OneID");

        SimpleHttp userInfoRequest = SimpleHttp.doPost(userInfoUrl, session)
                .param(OAUTH2_CLIENT_ID_PARAMETER, getConfig().getClientId())
                .param(OAUTH2_CLIENT_SECRET_PARAMETER, getConfig().getClientSecret())
                .param(OAUTH2_GRANT_TYPE_PARAMETER, OAUTH2_ACCESS_TOKEN_IDENTIFY)
                .param(OAUTH2_ACCESS_TOKEN_PARAMETER, subjectToken)
                .param(OAUTH2_SCOPE_PARAMETER, getConfig().getDefaultScope());

        logger.debug("Userinfo URL is successfully created. URL: " + userInfoRequest.toString());
        return userInfoRequest;
    }

    @Override
    public Object callback(RealmModel realm, AuthenticationCallback callback, EventBuilder event) {
        return new OneIdEndpoint(callback, realm, event, this);
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        logger.debug("Start getting userinfo with access token");

        OneIdUserDTO user = fetchUserInfo(accessToken);

        BrokeredIdentityContext context = new BrokeredIdentityContext(user.getUserId());
        context.setEmail(user.getEmail());
        context.setIdp(this);
        context.setIdpConfig(getConfig());
        context.setUsername(user.getUserId());
        context.setFirstName(user.getFirstName());
        context.setLastName(user.getSurname());

        logger.debug("User if fetch successfully! UserID: " + user.getUserId());
        return context;
    }

    private OneIdUserDTO fetchUserInfo(String accessToken) {

        SimpleHttp.Response response = null;
        try {
            response = buildUserInfoRequest(accessToken, getConfig().getUserInfoUrl()).asResponse();
            return response.asJson(OneIdUserDTO.class);
        } catch (Exception e) {
            logger.error("Error fetching userinfo from OneID. Exception: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static class OneIdEndpoint extends Endpoint {
        private final OneIdProvider provider;

        public OneIdEndpoint(AuthenticationCallback callback, RealmModel realm, EventBuilder event, OneIdProvider provider) {
            super(callback, realm, event, provider);
            this.provider = provider;
        }

        @Override
        public Response authResponse(@QueryParam("state") String state,
                                     @QueryParam("code") String authorizationCode,
                                     @QueryParam("error") String error) {
            logger.debug("REST request to get access token via authorization code. Code: " + authorizationCode);
            return super.authResponse(state, authorizationCode, error);
        }

        @Override
        public SimpleHttp generateTokenRequest(String code) {
            logger.debug("Start generating token via temporary code. Code: " + code);
            KeycloakContext context = session.getContext();

            String redirectUri = Urls.identityProviderAuthnResponse(
                    context.getUri().getBaseUri(),
                    provider.getConfig().getAlias(),
                    context.getRealm().getName()).toString();

            logger.debug("Redirect URI is created for getting token. Redirect URI: " + redirectUri);

            SimpleHttp tokenRequest = SimpleHttp.doPost(provider.getConfig().getTokenUrl(), session)
                    .param(OAUTH2_GRANT_TYPE_PARAMETER, OAUTH2_AUTHORIZATION_CODE)
                    .param(OAUTH2_CLIENT_ID_PARAMETER, provider.getConfig().getClientId())
                    .param(OAUTH2_CLIENT_SECRET_PARAMETER, provider.getConfig().getClientSecret())
                    .param(OAUTH2_CODE, code)
                    .param(OAUTH2_REDIRECT_URI_PARAMETER, redirectUri);

            return provider.authenticateTokenRequest(tokenRequest);
        }
    }

}
