package com.example.sportsdashboard.controller;

import com.example.sportsdashboard.model.Match;
import com.example.sportsdashboard.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping("/matches")
    public List<Match> getMatches() {
        return matchService.getStoredMatches();
    }

    @PostMapping("/matches/refresh")
    public ResponseEntity<List<Match>> refreshMatches() {
        List<Match> matches = matchService.refreshFromApi();
        return ResponseEntity.ok(matches);
    }

    @GetMapping("/standings")
    public List<MatchService.Standing> getStandings() {
        return matchService.getStandings();
    }

    @GetMapping("/teams/{teamId}/matches")
    public List<Match> getTeamMatches(@PathVariable String teamId) {
        return matchService.getTeamMatches(teamId);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }
}
