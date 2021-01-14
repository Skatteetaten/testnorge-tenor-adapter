package no.skatteetaten.testnorge.eksempeladapter.service;

import java.net.URI;

import no.skatteetaten.testnorge.tenor.ApiClient;
import no.skatteetaten.testnorge.tenor.api.DatasettApiApi;
import no.skatteetaten.testnorge.tenor.api.DatasettVersjonApiApi;
import no.skatteetaten.testnorge.tenor.api.TestdataApiApi;

public class TenorApi {
    private TenorConfiguration configuration;
    private ApiClient apiClient;

    public TenorApi(TenorConfiguration configuration) {
        this.configuration = configuration;
        apiClient = createClient();
    }

    private ApiClient createClient() {
        ApiClient apiClient = new ApiClient();
        URI uri = URI.create(configuration.tenorUrl());
        apiClient.setHost(uri.getHost());
        apiClient.setBasePath(uri.getPath());
        apiClient.setPort(uri.getPort());
        apiClient.setScheme(uri.getScheme());
        apiClient.setRequestInterceptor(maskinportenTokenProvider());
        return apiClient;
    }

    private MaskinportenTokenProvider maskinportenTokenProvider() {
        return MaskinportenTokenProvider.test(configuration);
    }

    public DatasettApiApi datasettApi() {
        return new DatasettApiApi(apiClient);
    }

    public DatasettVersjonApiApi datasettVersjonApi() {
        return new DatasettVersjonApiApi(apiClient);
    }

    public TestdataApiApi testdataApiApi() {
        return new TestdataApiApi(apiClient);
    }
}
