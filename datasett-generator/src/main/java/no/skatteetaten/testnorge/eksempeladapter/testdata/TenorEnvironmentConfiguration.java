package no.skatteetaten.testnorge.eksempeladapter.testdata;

public class TenorEnvironmentConfiguration implements TenorConfiguration {

    @Override
    public String tenorUrl() {
        return required("tenor.url");
    }

    @Override
    public String orgno() {
        return required("tenor.orgno");
    }

    @Override
    public String clientId() {
        return required("tenor.maskinporten.clientId");
    }

    @Override
    public String certificate() {
        return required("tenor.maskinporten.certificate");
    }

    @Override
    public String keyId() {
        return property("tenor.maskinporten.keyid");
    }

    private String required(String key) {
        String property = property(key);
        if (property == null || property.isEmpty()) {
            System.err.println(String.format("Nøkkel %s/%s er obligatorisk", key, toEnvKey(key)));
            throw new RuntimeException(String.format("Nøkkel %s/%s er obligatorisk", key, toEnvKey(key)));
        }
        return property;
    }

    private String property(String key) {
        return System.getProperty(key, System.getenv(toEnvKey(key)));
    }

    private String toEnvKey(String key) {
        return key.replace(".", "_")
            .replaceAll("([A-Z])", "_$1")
            .toUpperCase();
    }
}
