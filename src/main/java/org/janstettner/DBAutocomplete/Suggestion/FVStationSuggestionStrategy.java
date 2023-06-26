package org.janstettner.DBAutocomplete.Suggestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.janstettner.DBAutocomplete.DTO.SuggestOutput;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.query_dsl.QueryBuilders;
import org.opensearch.client.opensearch.core.MsearchRequest;
import org.opensearch.client.opensearch.core.MsearchResponse;
import org.opensearch.client.opensearch.core.msearch.MultisearchBody;
import org.opensearch.client.opensearch.core.msearch.MultisearchHeader;
import org.opensearch.client.opensearch.core.msearch.RequestItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FVStationSuggestionStrategy {
    private final OpenSearchClient client;

    public List<SuggestOutput> querySuggestions(String suggestInput) throws IOException {
        var searchResult = msearchSuggestQuery(suggestInput);

        var stationSuggestions = new ArrayList<SuggestOutput>();

        return stationSuggestions;
    }

    private MsearchResponse<Object> msearchSuggestQuery(String suggestInput) throws IOException {
        var autoFuzzyQuery = QueryBuilders.fuzzy()
                .fuzziness("auto")
                .field("suggestions")
                .value(FieldValue.of(normalize(suggestInput)))
                .build()._toQuery();
        var zeroFuzzyQuery = QueryBuilders.fuzzy()
                .fuzziness("0")
                .field("suggestions")
                .value(FieldValue.of(normalize(suggestInput)))
                .build()._toQuery();

        return client.msearch(MsearchRequest.of(ms -> ms.searches(
                        List.of(RequestItem.of(ri -> ri
                                        .header(MultisearchHeader.of(mh -> mh.index("stations")))
                                        .body(MultisearchBody.of(msb -> msb.query(autoFuzzyQuery))
                                        )),
                                RequestItem.of(ri -> ri
                                        .header(MultisearchHeader.of(mh -> mh.index("stations")))
                                        .body(MultisearchBody.of(msb -> msb.query(zeroFuzzyQuery))
                                        )))
                )),
                Object.class);
    }

    private String normalize(String input) {
        return input.trim().toLowerCase().replaceAll("[\\s.,-]+", " ");
    }
}
