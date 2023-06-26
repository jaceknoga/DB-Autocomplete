package org.janstettner.DBAutocomplete.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.janstettner.DBAutocomplete.DTO.SuggestOutput;
import org.janstettner.DBAutocomplete.Suggestion.FVStationSuggestionStrategy;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
@Slf4j
@RequiredArgsConstructor
public class AutocompleteController {
    private final OpenSearchClient client;
    private final DescriptiveStatistics stats = new DescriptiveStatistics();

    // TODO: Add OpenAPI Swagger
//    @Operation(summary = "Suggests completions for Deutsche Bahn stations from an input")
    @GetMapping(path = "/auto-complete/{input}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Object> autoComplete(
            @PathVariable String input,
//            @Validated @RequestHeader(name = "x-station-type", defaultValue = StationType.ALL) StationType stationType,
            @RequestHeader("x-api-key") String apiKey
    ) throws IOException {
        if (!Objects.equals(apiKey, "24DB-Sprintstart42")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        var start = System.currentTimeMillis();
        var result = createResponse(input);
        stats.addValue(System.currentTimeMillis() - start);
        if (stats.getN() % 10 == 0) {
            // TODO: This log output doesn't seem to work, or the if-clause is faulty
            log.debug(String.valueOf(stats));
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    private List<SuggestOutput> createResponse(String input) throws IOException {
        var strategy = new
                FVStationSuggestionStrategy(client);
        return strategy.querySuggestions(input);
    }
}