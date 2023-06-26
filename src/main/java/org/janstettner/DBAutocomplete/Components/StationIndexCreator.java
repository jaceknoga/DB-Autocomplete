package org.janstettner.DBAutocomplete.Components;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.janstettner.DBAutocomplete.Exceptions.OpenSearchHealthException;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.HealthStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class StationIndexCreator {
    private final OpenSearchClient client;
    private final StationDataLoader dataLoader;
    private final CloseableHttpClient httpClient;

    @PostConstruct
    public void init() throws IOException {
        log.info("Checking OpenSearch cluster health.");
        checkClusterHealth();

        // TODO: delete index automatically
        log.info("Starting to create OpenSearch index!");
        var indexName = "stations";

        String jsonMapping;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("stations.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
            jsonMapping = mapper.writeValueAsString(jsonNode);
            log.info(jsonMapping);
        } catch (Exception e) {
            throw new IOException(e);
        }

        var putRequest = new HttpPut(URI.create("http://localhost:9200/" + indexName));
        putRequest.setHeader("Content-Type", "application/json");
        putRequest.setEntity(new StringEntity(jsonMapping));
        var response = httpClient.execute(putRequest, new BasicHttpContext(), Object::toString);
        log.info(response);

        log.info("Index created!");

        dataLoader.load();
    }

    public void checkClusterHealth() throws IOException {
        var healthStatus = client.cluster().health().status();
        if (healthStatus == HealthStatus.Red) {
            throw new OpenSearchHealthException();
        }
        log.info("Cluster health is {}.", healthStatus);
    }
}
