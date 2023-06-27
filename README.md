# DB-Autocomplete
Demonstration project with SpringBoot and Elasticsearch / OpenSearch to provide an autocomplete REST endpoint from a dataset of DB stations.

### Prerequisites
- Docker with Compose V2 installed. For Compose V1 use "docker-compose" instead.

### Setup
Start the development environment by running:
```shell
docker compose up -d --build
```
This should build two containers, springboot and opensearch.

Requests to the autocomplete endpoint can be sent e.g. with cURL:
```shell
curl --location 'http://localhost:8080/api/v1/auto-complete/aac'
```

### Troubleshooting
In the rare case that the Spring Boot container starts before OpenSearch is ready, simply restart Spring Boot:
```shell
docker compose up -d
```
This could only happen after the very first build of the images,
when OpenSearch needs a minute or two to load, so a second start of the container should fix the problem.

### Acknowledgements
Due to the limited amount of time, some (additional) features are not fully tested and implemented:
- TODO items are left intentionally in the code, as they document improvements or possible future features:
  - The Spring Boot image is quite large, which should be improved with a JDK/JRE that is better suited for this purpose
  - With the permutations, it would also be possible to search for the EVA_NR and DS100 fields
  - The weighting and score building of indexing and querying OpenSearch documents would improve search results
  - Additional SuggestStrategies (see [FVStationSuggestionStrategy](src/main/java/org/janstettner/DBAutocomplete/Suggestion/FVStationSuggestionStrategy.java)) could provide autocomplete features only to e.g. "RV" stations
- There is only one unit test and no integration tests yet
- A deployment to AWS using a GitHub CI/CS pipeline was planned, but not implemented in time due to the incomplete documentation of the OpenSearch Java Client
- The basic API key check is disabled, as a deployment to AWS did not take place