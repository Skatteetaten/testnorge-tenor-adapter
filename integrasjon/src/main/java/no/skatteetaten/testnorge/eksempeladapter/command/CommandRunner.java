package no.skatteetaten.testnorge.eksempeladapter.command;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.skatteetaten.testnorge.eksempeladapter.service.TenorApi;
import no.skatteetaten.testnorge.eksempeladapter.service.TenorConfiguration;
import no.skatteetaten.testnorge.eksempeladapter.service.TenorEnvironmentConfiguration;

public abstract class CommandRunner {

    public abstract void usage();

    public abstract void run(String[] args) throws Exception;

    public TenorConfiguration tenorConfiguration() {
        return new TenorEnvironmentConfiguration();
    }

    public TenorApi tenorApi() {
        return new TenorApi(tenorConfiguration());
    }

    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    public String stdin() throws IOException {
        return new String(System.in.readAllBytes());
    }

    public <T> T stdin(Class<T> tClass) throws IOException {
        return objectMapper().readValue(stdin(), tClass);
    }

    public Iterator<JsonNode> stdinBulk() throws IOException {
        return objectMapper().readValues(new JsonFactory().createParser(stdin()), JsonNode.class);
    }
}
