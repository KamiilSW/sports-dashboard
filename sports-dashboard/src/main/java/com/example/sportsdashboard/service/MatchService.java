package com.example.sportsdashboard.service;

import com.example.sportsdashboard.config.FootballDataProperties;
import com.example.sportsdashboard.model.Match;
import com.example.sportsdashboard.repository.MatchRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final FootballDataProperties footballDataProperties;
    private final RestTemplate restTemplate;

    public MatchService(MatchRepository matchRepository, FootballDataProperties footballDataProperties) {
        this.matchRepository = matchRepository;
        this.footballDataProperties = footballDataProperties;
        this.restTemplate = new RestTemplate();
    }

    public List<Match> getStoredMatches() {
        return matchRepository.findAllByOrderByDateDesc();
    }

    public List<Match> refreshFromApi() {
        return getTeamMatches("57");
    }

    public List<Standing> getStandings() {
        String url = footballDataProperties.getBaseUrl() + "/competitions/PL/standings?standingType=TOTAL";
        FootballDataStandingsResponse response = getFromApi(url, FootballDataStandingsResponse.class);
        if (response == null || response.getStandings() == null || response.getStandings().isEmpty()) {
            return List.of();
        }
        return response.getStandings().get(0).getTable();
    }

    public List<Match> getTeamMatches(String teamId) {
        String url = footballDataProperties.getBaseUrl()
                + "/teams/" + teamId + "/matches?status=FINISHED&limit=10";
        FootballDataResponse response = getFromApi(url, FootballDataResponse.class);
        if (response == null || response.getMatches() == null) {
            return List.of();
        }

        List<Match> matches = new ArrayList<>();
        for (FootballDataMatch dto : response.getMatches()) {
            Match match = toMatch(dto);
            if (match == null) {
                continue;
            }

            Match storedMatch = matchRepository.findByExternalMatchId(match.getExternalMatchId()).orElse(null);
            matches.add(storedMatch != null ? storedMatch : matchRepository.save(match));
        }
        return matches;
    }

    private <T> T getFromApi(String url, Class<T> responseType) {
        String apiKey = footballDataProperties.getApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Football-Data API key is not configured. Set FOOTBALL_DATA_API_KEY or football-data.api-key.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Auth-Token", apiKey);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                requestEntity,
                responseType
            );
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            String errorMessage = extractApiErrorMessage(ex.getResponseBodyAsString());
            throw new IllegalStateException(errorMessage != null ? errorMessage : ex.getMessage(), ex);
        }
    }

    private Match toMatch(FootballDataMatch dto) {
        if (dto.getId() == null || dto.getUtcDate() == null || dto.getHomeTeam() == null || dto.getAwayTeam() == null) {
            return null;
        }

        FullTime fullTime = dto.getScore() != null ? dto.getScore().getFullTime() : null;
        int homeScore = fullTime != null && fullTime.getHome() != null ? fullTime.getHome() : 0;
        int awayScore = fullTime != null && fullTime.getAway() != null ? fullTime.getAway() : 0;
        LocalDateTime matchDate = LocalDateTime.parse(dto.getUtcDate(), DateTimeFormatter.ISO_DATE_TIME);
        return new Match(dto.getId(), matchDate, dto.getHomeTeam().getName(), dto.getAwayTeam().getName(), homeScore, awayScore);
    }

    private String extractApiErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        try {
            JsonNode root = new ObjectMapper().readTree(responseBody);
            if (root.has("message") && !root.get("message").isNull()) {
                return root.get("message").asText();
            }
            if (root.has("error") && !root.get("error").isNull()) {
                return root.get("error").asText();
            }
        } catch (JsonProcessingException ignored) {
            return responseBody;
        }

        return responseBody;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataResponse {
        @JsonProperty("matches")
        private List<FootballDataMatch> matches;

        public List<FootballDataMatch> getMatches() {
            return matches;
        }

        public void setMatches(List<FootballDataMatch> matches) {
            this.matches = matches;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataStandingsResponse {
        @JsonProperty("standings")
        private List<StandingGroup> standings;

        public List<StandingGroup> getStandings() {
            return standings;
        }

        public void setStandings(List<StandingGroup> standings) {
            this.standings = standings;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StandingGroup {
        @JsonProperty("table")
        private List<Standing> table;

        public List<Standing> getTable() {
            return table;
        }

        public void setTable(List<Standing> table) {
            this.table = table;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Standing {
        private int position;
        private Team team;
        private int playedGames;
        private int won;
        private int draw;
        private int lost;
        private int points;
        private int goalsFor;
        private int goalsAgainst;
        private int goalDifference;

        public int getPosition() { return position; }
        public void setPosition(int position) { this.position = position; }
        public Team getTeam() { return team; }
        public void setTeam(Team team) { this.team = team; }
        public int getPlayedGames() { return playedGames; }
        public void setPlayedGames(int playedGames) { this.playedGames = playedGames; }
        public int getWon() { return won; }
        public void setWon(int won) { this.won = won; }
        public int getDraw() { return draw; }
        public void setDraw(int draw) { this.draw = draw; }
        public int getLost() { return lost; }
        public void setLost(int lost) { this.lost = lost; }
        public int getPoints() { return points; }
        public void setPoints(int points) { this.points = points; }
        public int getGoalsFor() { return goalsFor; }
        public void setGoalsFor(int goalsFor) { this.goalsFor = goalsFor; }
        public int getGoalsAgainst() { return goalsAgainst; }
        public void setGoalsAgainst(int goalsAgainst) { this.goalsAgainst = goalsAgainst; }
        public int getGoalDifference() { return goalDifference; }
        public void setGoalDifference(int goalDifference) { this.goalDifference = goalDifference; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FootballDataMatch {
        @JsonProperty("id")
        private String id;

        @JsonProperty("utcDate")
        private String utcDate;

        @JsonProperty("homeTeam")
        private Team homeTeam;

        @JsonProperty("awayTeam")
        private Team awayTeam;

        @JsonProperty("score")
        private Score score;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUtcDate() {
            return utcDate;
        }

        public void setUtcDate(String utcDate) {
            this.utcDate = utcDate;
        }

        public Team getHomeTeam() {
            return homeTeam;
        }

        public void setHomeTeam(Team homeTeam) {
            this.homeTeam = homeTeam;
        }

        public Team getAwayTeam() {
            return awayTeam;
        }

        public void setAwayTeam(Team awayTeam) {
            this.awayTeam = awayTeam;
        }

        public Score getScore() {
            return score;
        }

        public void setScore(Score score) {
            this.score = score;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("crest")
        private String crest;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCrest() {
            return crest;
        }

        public void setCrest(String crest) {
            this.crest = crest;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Score {
        @JsonProperty("fullTime")
        private FullTime fullTime;

        public FullTime getFullTime() {
            return fullTime;
        }

        public void setFullTime(FullTime fullTime) {
            this.fullTime = fullTime;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FullTime {
        @JsonProperty("home")
        private Integer home;

        @JsonProperty("away")
        private Integer away;

        public Integer getHome() {
            return home;
        }

        public void setHome(Integer home) {
            this.home = home;
        }

        public Integer getAway() {
            return away;
        }

        public void setAway(Integer away) {
            this.away = away;
        }
    }
}
