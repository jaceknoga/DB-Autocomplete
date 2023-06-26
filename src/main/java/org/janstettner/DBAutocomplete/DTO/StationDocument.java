package org.janstettner.DBAutocomplete.DTO;

import java.util.List;
import java.util.Objects;

public record StationDocument(Station station, List<String> suggestions) {
    public StationDocument {
        Objects.requireNonNull(station);
        Objects.requireNonNull(suggestions);
    }
}
