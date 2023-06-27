package org.janstettner.DBAutocomplete.DTO;

import java.util.List;

public record AutocompleteResponse(List<String> station_list, String time_taken, String number_of_stations_found) {
}
