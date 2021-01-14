package no.skatteetaten.testnorge.eksempeladapter.service;

public class TenorEnvironmentConfiguration implements TenorConfiguration {

    @Override
    public String tenorUrl() {
        return required("tenor.url");
    }

    @Override
    public String tenorIdentifikator() {
        return required("tenor.identifikator");
    }

    @Override
    public String clientId() {
        return required("tenor.maskinporten.clientId");
    }

    @Override
    public String jksPath() {
        return required("tenor.maskinporten.jks.path");
    }

    @Override
    public String jksAlias() {
        return required("tenor.maskinporten.jks.alias");
    }

    @Override
    public String jksPassword() {
        return required("tenor.maskinporten.jks.password");
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
