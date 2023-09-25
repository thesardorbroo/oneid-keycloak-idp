package uz.sardorbroo.keycloak.authenticator;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

public class OneIdIdentityProviderFactory extends AbstractIdentityProviderFactory<OneIdProvider> {

    private final static String ID = "oneid";
    private final static String NAME = "OneID";

    private final static OneIdProviderConfig CONFIG = new OneIdProviderConfig();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public OneIdProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new OneIdProvider(session, CONFIG);
    }

    @Override
    public IdentityProviderModel createConfig() {
        return CONFIG;
    }

    @Override
    public String getId() {
        return ID;
    }
}
