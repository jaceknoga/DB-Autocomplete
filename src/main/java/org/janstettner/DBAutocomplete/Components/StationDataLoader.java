package org.janstettner.DBAutocomplete.Components;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.iterators.PermutationIterator;
import org.janstettner.DBAutocomplete.DTO.Station;
import org.janstettner.DBAutocomplete.DTO.StationDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class StationDataLoader {
    private final OpenSearchClient client;
    private final RequestServices requestServices;

    public void load() throws IOException {
        var stationDocuments = readStationData();
        var request = new BulkRequest.Builder();
        for (var doc : stationDocuments) {
            // TODO indexName as env var
            // TODO add higher weight to FV and RV stations
            request.operations(operation -> operation
                    .index(idx -> idx
                            .index("stations")
                            .document(doc))
            );
        }
        requestServices.exponentialTimeoutRequest(request.build());
        var flushResponse = client.indices().flush();
        log.info("FlushResponse shards failed: {}", flushResponse.shards().failed());
        log.info("Loaded {} documents.", stationDocuments.size());

    }

    private HashSet<StationDocument> readStationData() throws IOException {
        // TODO env var
        var inputStream = getClass().getClassLoader().getResourceAsStream("D_Bahnhof_2020_alle.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)));

        // HashSet to avoid duplicates
        var stations = new HashSet<StationDocument>();
        var numLines = 0;
        String line;

        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            numLines += 1;

            // temporary filter for Fernverkehr
            // parts[3] for 2016 data
            if (!Objects.equals(parts[4], "FV")) {
                continue;
            }
            // add data from CSV to the Station data transfer object
            // for D_Bahnhof_2016_01_alle.csv: (parts[0], parts[1], parts[2], parts[3])
            var station = new Station(parts[0], parts[1], parts[3], parts[4]);
            stations.add(new StationDocument(station, createSuggestions(station)));
        }
        log.info("Read {} lines.", numLines);

        reader.close();

        return stations;
    }

    private static List<String> createSuggestions(Station station) {
        var parts = new ArrayList<>(List.of(
                station.evaNr(),
                station.ds100(),
                station.name()));
        return createPermutations(parts);
    }

    public static List<String> createPermutations(List<String> parts) {
        var permutations = new ArrayList<String>();
        var iterator = new PermutationIterator<>(parts);
        while (iterator.hasNext()) {
            permutations.add(String.join(" ", iterator.next()));
        }
        return permutations;
    }
}
