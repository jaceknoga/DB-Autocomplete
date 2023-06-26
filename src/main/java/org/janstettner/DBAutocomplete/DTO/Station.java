package org.janstettner.DBAutocomplete.DTO;

import java.util.Objects;

public record Station(String evaNr, String ds100, String name, String verkehr) {
    public Station {
        Objects.requireNonNull(evaNr);
        Objects.requireNonNull(ds100);
        Objects.requireNonNull(name);
        Objects.requireNonNull(verkehr);
    }
}
