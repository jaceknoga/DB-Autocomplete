package org.janstettner.DBAutocomplete.Configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppConfiguration {
    private String apiKey;
    private String indexFilename;
    private String stationDataFilename;
}