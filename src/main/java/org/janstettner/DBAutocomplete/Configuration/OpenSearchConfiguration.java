package org.janstettner.DBAutocomplete.Configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "opensearch")
@Data
public class OpenSearchConfiguration {
    private String host;
    private String port;
    private String index;
}