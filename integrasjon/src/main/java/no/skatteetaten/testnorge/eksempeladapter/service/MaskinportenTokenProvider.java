package no.skatteetaten.testnorge.eksempeladapter.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.http.HttpRequest;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.JWTBearerGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerConfigurationRequest;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.id.Issuer;

/**
 * Eksempel integrasjon mot maskinporten med nimbus oauth2 sdk.
 * <p>
 * For andre biblioteker og løsninger se https://oauth.net/code/
 * <p>
 * Maskinporten dokumentasjon: https://difi.github.io/felleslosninger/maskinporten_overordnet.html
 */
public class MaskinportenTokenProvider implements Consumer<HttpRequest.Builder> {

    public static final String VER_2_MASKINPORTEN = "https://ver2.maskinporten.no/";
    public static final String SCOPE_SKATTEETATEN_TESTNORGE_TESTDATA_WRITE = "skatteetaten:testnorge/testdata.write";
    public static final int EXPIRE_MINUTES = 2;

    private final Issuer issuer;
    private final String scopes;
    private final int expireMinutes;
    private final TenorConfiguration tenorConfiguration;

    private AuthorizationServerMetadata authorizationServerMetadata;
    private SignedJWT requestToken;
    private LocalDateTime expires;
    private String accessToken;

    public static MaskinportenTokenProvider test(TenorConfiguration tenorConfiguration) {
        return new MaskinportenTokenProvider(
            VER_2_MASKINPORTEN,
            SCOPE_SKATTEETATEN_TESTNORGE_TESTDATA_WRITE,
            EXPIRE_MINUTES,
            tenorConfiguration);
    }

    public MaskinportenTokenProvider(String issuer, String scopes, int expireMinutes,
        TenorConfiguration tenorConfiguration) {
        this.issuer = new Issuer(issuer);
        this.scopes = scopes;
        this.expireMinutes = expireMinutes;
        this.tenorConfiguration = tenorConfiguration;
    }

    /**
     * Hent ut access token.
     * <p>
     * Henter ut et nytt access token dersom det ikke finnes fra før, eller access token er utløpt.
     */
    @Override
    public void accept(HttpRequest.Builder builder) {
        if (this.accessToken == null || expires.isBefore(LocalDateTime.now())) {
            hentServerMetadata();
            genererRequestToken();
            hentAccessToken();
        }
        builder.header("Authorization", "Bearer " + this.accessToken);
    }

    private RSAKey load() {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(tenorConfiguration.jksPath()), tenorConfiguration.jksPassword().toCharArray());
            RSAKey key = RSAKey.load(keyStore, tenorConfiguration.jksAlias(), tenorConfiguration.jksPassword().toCharArray());
            return key;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | JOSEException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void genererRequestToken() {
        // RSA signatures require a public and private RSA key pair, the public key
        // must be made known to the JWS recipient in order to verify the signatures
        RSAKey rsaJWK = load();

        // Create RSA-signer with the private key
        JWSSigner signer = null;
        try {
            signer = new RSASSASigner(rsaJWK);
        } catch (
            JOSEException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        LocalDateTime now = LocalDateTime.now();
        this.expires = now.plus(expireMinutes, ChronoUnit.MINUTES);

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .issuer(tenorConfiguration.clientId())
            .audience(issuer.getValue())
            .issueTime(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
            .expirationTime(Date.from(this.expires.atZone(ZoneId.systemDefault()).toInstant()))
            .jwtID(UUID.randomUUID().toString())
            .claim("scope", scopes)
            .build();

        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
            claimsSet);

        // Compute the RSA signature
        try {
            signedJWT.sign(signer);
        } catch (
            JOSEException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        this.requestToken = signedJWT;
    }

    private void hentAccessToken() {
        JWTBearerGrant jwtBearerGrant = new JWTBearerGrant(this.requestToken);

        TokenRequest tokenRequest = new TokenRequest(authorizationServerMetadata.getTokenEndpointURI(), jwtBearerGrant);

        TokenResponse parse1 = null;
        try {
            parse1 = TokenResponse.parse(tokenRequest.toHTTPRequest().send());
        } catch (ParseException | IOException e) {
            throw new RuntimeException("Klarte ikke hente token", e);
        }

        if (!parse1.indicatesSuccess()) {
            throw new RuntimeException(parse1.toErrorResponse().toJSONObject().toJSONString());
        }
        this.accessToken = parse1.toSuccessResponse().getTokens().getBearerAccessToken().toString();
    }

    private void hentServerMetadata() {
        if (authorizationServerMetadata != null) {
            return;
        }
        AuthorizationServerConfigurationRequest authorizationServerConfigurationRequest =
            new AuthorizationServerConfigurationRequest(issuer);
        try {
            this.authorizationServerMetadata = AuthorizationServerMetadata
                .parse(authorizationServerConfigurationRequest.toHTTPRequest().send().getContent());
        } catch (ParseException | IOException e) {
            throw new RuntimeException("Klarte ikke hente server metadata", e);
        }
    }

}
