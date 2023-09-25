package uz.sardorbroo.keycloak.authenticator;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;

import java.util.Map;

public class OneIdProviderConfig extends OAuth2IdentityProviderConfig {
    private final static String ONE_ID_URL = "https://sso.egov.uz/sso/oauth/Authorization.do";

    /** Override CLIENT_ID and CLIENT_SECRET fields */
    private final static String CLIENT_ID = "";
    private final static String CLIENT_SECRET = ""; // It will use with "scope" parameter like "scope=openid ${CLIENT_SECRET}"
    private final static String SCOPE = "openid " + CLIENT_SECRET;
    private final static String PROVIDER_NAME = "oneid";
    private final static String PROVIDER_ID = "oneid";

    @Override
    public String getAuthorizationUrl() {
        return ONE_ID_URL;
    }

    @Override
    public String getTokenUrl() {
        return ONE_ID_URL;
    }

    @Override
    public String getUserInfoUrl() {
        return ONE_ID_URL;
    }

    @Override
    public String getClientId() {
        return CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CLIENT_SECRET;
    }

    @Override
    public String getDefaultScope() {
        return SCOPE;
    }

    @Override
    public boolean isAddReadTokenRoleOnCreate() {
        return true;
    }

    @Override
    public String getClientAuthMethod() {
        return "clientAuth_post";
    }

    @Override
    public boolean isPkceEnabled() {
        return false;
    }

    @Override
    public String getAlias() {
        return PROVIDER_ID;
    }

    @Override
    public String getProviderId() {
        return PROVIDER_ID;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isStoreToken() {
        return true;
    }

    @Override
    public Map<String, String> getConfig() {
        return super.getConfig();
    }

    @Override
    public boolean isTrustEmail() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return PROVIDER_NAME;
    }


}
