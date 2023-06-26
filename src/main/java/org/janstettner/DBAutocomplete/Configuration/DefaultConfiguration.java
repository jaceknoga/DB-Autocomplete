package org.janstettner.DBAutocomplete.Configuration;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultConfiguration {
    @Bean
    public OpenSearchClient getOpenSearchClient() {
        final HttpHost host = new HttpHost("http", "localhost", 9200);

        final ApacheHttpClient5TransportBuilder builder = ApacheHttpClient5TransportBuilder.builder(host);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            final PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
                    .create()
                    .build();

            return httpClientBuilder
                    .setConnectionManager(connectionManager);
        });

        final OpenSearchTransport transport = ApacheHttpClient5TransportBuilder.builder(host).build();
        return new OpenSearchClient(transport);
    }

    @Bean
    public CloseableHttpClient getHttpClient() {
        return HttpClients.createDefault();
    }
}