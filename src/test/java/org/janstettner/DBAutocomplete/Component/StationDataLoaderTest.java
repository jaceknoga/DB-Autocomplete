package org.janstettner.DBAutocomplete.Component;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest(properties="spring.main.lazy-initialization=true")
class StationDataLoaderTest {

    @Test
    public void testCreatePermutations() {
        ArrayList<String> parts = new ArrayList<>(Arrays.asList("quick", "brown", "fox"));
        List<String> expectedPermutations = Arrays.asList(
                "quick brown fox",
                "quick fox brown",
                "fox quick brown",
                "fox brown quick",
                "brown fox quick",
                "brown quick fox"
        );
        List<String> actualPermutations = StationDataLoader.createPermutations(parts);
        assertEquals(expectedPermutations, actualPermutations);
    }

//    @Test
//    public void testCreatePermutationsWithEmptyList() {
//        ArrayList<String> parts = new ArrayList<>();
//        List<String> actualPermutations = StationDataLoader.createPermutations(parts);
//        assertEquals(0, actualPermutations.size());
//    }

}