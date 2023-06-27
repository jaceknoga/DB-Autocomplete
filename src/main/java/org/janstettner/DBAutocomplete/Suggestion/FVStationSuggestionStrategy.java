package org.janstettner.DBAutocomplete.Suggestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.janstettner.DBAutocomplete.Configuration.AppNames;
import org.janstettner.DBAutocomplete.Configuration.AppNames.Suggestion;
import org.janstettner.DBAutocomplete.DTO.Station;
import org.janstettner.DBAutocomplete.DTO.StationDocument;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.FieldSuggester;
import org.opensearch.client.opensearch.core.search.SuggestFuzziness;
import org.opensearch.client.opensearch.core.search.Suggester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FVStationSuggestionStrategy {
    private final OpenSearchClient client;

    public List<String> querySuggestions(String suggestInput) throws IOException {
        var searchResponse = searchSuggestQuery(suggestInput);

        return getSuggestionsFromResponse(searchResponse);
    }

    private SearchResponse<StationDocument> searchSuggestQuery(String suggestInput) throws IOException {
        var fieldSuggesterMap = new HashMap<String, FieldSuggester>();
        fieldSuggesterMap.put(Suggestion.AUTO_FUZZY_TITLE, FieldSuggester.of(fs -> fs
                .completion(cs -> cs.skipDuplicates(true)
                        .fuzzy(SuggestFuzziness.of(sf -> sf
                                .fuzziness("AUTO")
                                .transpositions(true)
                                .minLength(3) // Is already the default value
                                .prefixLength(1)
                                .unicodeAware(true)
                        ))
                        .field(Suggestion.SUGGESTIONS_FIELD))
        ));
        Suggester suggester = Suggester.of(s -> s
                .suggesters(fieldSuggesterMap)
                .text(normalize(suggestInput))
        );
        SearchRequest searchRequest = SearchRequest.of(s -> s
                .index(AppNames.Index.INDEX_NAME)
//                    .source(SourceConfig.of(sc -> sc.filter(f -> f.includes(List.of(Suggestion.NAME_FIELD)))))
                .suggest(suggester));
        return client.search(searchRequest, StationDocument.class);


        // A multi-search approach would have been my preferred method, but unfortunately, the documentation
        // for the new Java Client is, as of now, only covering a few basics. Theoretically this way, we could
        // send two queries with zero fuzziness and with auto fuzziness without negatively effecting response times

        // return client.msearch(MsearchRequest.of(ms -> ms.searches(
        //                 List.of(RequestItem.of(ri -> ri
        //                         .header(MultisearchHeader.of(mh -> mh.index("stations")))
        //                         .body(MultisearchBody.of(msb -> msb.query(boolQuery))
        //                         )))
        //         )),
        //         Object.class);
    }

    private String normalize(String input) {
        return input.trim().toLowerCase().replaceAll("[\\s.,-]+", " ");
    }

    public List<String> getSuggestionsFromResponse(SearchResponse<StationDocument> searchResponse) {
        var stationSuggestions = new ArrayList<String>();
        var suggestions = searchResponse.suggest().get(Suggestion.COMPLETION_PREFIX + Suggestion.AUTO_FUZZY_TITLE);
        for (var item : suggestions) {
            stationSuggestions.addAll(item
                    .options()
                    .stream().map(o ->
                            createSuggestOutputFromStation(o.completion().source().station()))
                            .distinct()
                    .toList());
        }
        return stationSuggestions;
    }

    private String createSuggestOutputFromStation(Station station) {
        return String.join(" - ", station.evaNr(), station.ds100(), station.name());
    }
}
