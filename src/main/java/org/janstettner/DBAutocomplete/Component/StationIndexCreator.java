package org.janstettner.DBAutocomplete.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.janstettner.DBAutocomplete.Configuration.AppConfiguration;
import org.janstettner.DBAutocomplete.Configuration.OpenSearchConfiguration;
import org.janstettner.DBAutocomplete.Exception.OpenSearchHealthException;
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
    private final OpenSearchConfiguration openSearchConfiguration;
    private final AppConfiguration appConfiguration;

    @PostConstruct
    public void init() throws IOException {
        log.info("Checking OpenSearch cluster health.");
        checkClusterHealth();

        log.info("Deleting old OpenSearch index!");
        deleteIndex(openSearchConfiguration.getIndex());

        log.info("Starting to create new OpenSearch index!");
        createIndex(openSearchConfiguration.getIndex());

        log.info("Starting to load data into index.");
        dataLoader.load();
    }

    private void deleteIndex(String indexName) throws IOException {
        var deleteRequest = new HttpDelete(getIndexUri());
        var responseCode = httpClient.execute(deleteRequest, new BasicHttpContext(), HttpResponse::getCode);
        switch (responseCode) {
            case 404 -> log.info("Index \"{}\" not found! Continuing...", indexName);
            case 200 -> log.info("Existing index \"{}\" deleted!", indexName);
            default -> log.warn("Unexpected response code from OpenSearch: {}. Original delete request: {}",
                    responseCode, deleteRequest);
        }
        client.indices().flush();
    }

    private void createIndex(String indexName) throws IOException {
        String jsonMapping;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(appConfiguration.getIndexFilename())) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
            jsonMapping = mapper.writeValueAsString(jsonNode);
        } catch (Exception e) {
            throw new IOException(e);
        }

        var putRequest = new HttpPut(getIndexUri());
        putRequest.setHeader("Content-Type", "application/json");
        putRequest.setEntity(new StringEntity(jsonMapping));
        var response = httpClient.execute(putRequest, new BasicHttpContext(), Object::toString);
        log.info(response);

        log.info("Index \"{}\" created!", indexName);
    }

    public void checkClusterHealth() throws IOException {
        var healthStatus = client.cluster().health().status();
        if (healthStatus == HealthStatus.Red) {
            throw new OpenSearchHealthException();
        }
        log.info("Cluster health is {}.", healthStatus);
    }

    private URI getIndexUri() {
        return URI.create("http://%s:%s/%s".formatted(
                openSearchConfiguration.getHost(),
                openSearchConfiguration.getPort(),
                openSearchConfiguration.getIndex()
        ));
    }
}
