package org.janstettner.DBAutocomplete.Configuration;

public final class AppNames {

    private AppNames() {
    }

    public static class Index {
        private Index() {
        }

        public static final String INDEX_NAME = "stations";
    }

    public static class Suggestion {
        private Suggestion() {
        }

        public static final String ZERO_FUZZY_TITLE = "zero-fuzziness";
        public static final String AUTO_FUZZY_TITLE = "auto-fuzziness";
        public static final String COMPLETION_PREFIX = "completion#";
        public static final String SUGGESTIONS_FIELD = "suggestions";
        public static final String NAME_FIELD = "name";
    }
}
