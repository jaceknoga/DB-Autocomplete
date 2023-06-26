package org.janstettner.DBAutocomplete.Components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RequestServices {
    private final OpenSearchClient client;
    public void exponentialTimeoutRequest(BulkRequest request) {
        var timeout = 1_000;
        while (true) {
            try {
                var response = client.bulk(request);
                log.info("Bulk request took {}ms. Errors: {}", response.took(), response.errors());
                return;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            log.info("Request failed! Sleeping for {} seconds...", timeout / 1_000);
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
                // TODO check if this works as intended (see SonarLint)
                Thread.currentThread().interrupt();
            }
            timeout *= 2;
        }
    }
}
