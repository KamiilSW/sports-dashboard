package com.example.sportsdashboard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "matches",
        uniqueConstraints = @UniqueConstraint(name = "uk_matches_external_match_id", columnNames = "external_match_id")
)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_match_id", nullable = false, unique = true)
    private String externalMatchId;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(name = "home_team", nullable = false)
    private String homeTeam;

    @Column(name = "away_team", nullable = false)
    private String awayTeam;

    @Column(name = "home_score", nullable = false)
    private int homeScore;

    @Column(name = "away_score", nullable = false)
    private int awayScore;

    @Column(name = "competition_name")
    private String competitionName;

    public Match() {
    }

    public Match(String externalMatchId, LocalDateTime date, String homeTeam, String awayTeam, int homeScore, int awayScore) {
        this(externalMatchId, date, homeTeam, awayTeam, homeScore, awayScore, null);
    }

    public Match(String externalMatchId, LocalDateTime date, String homeTeam, String awayTeam, int homeScore, int awayScore, String competitionName) {
        this.externalMatchId = externalMatchId;
        this.date = date;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.competitionName = competitionName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalMatchId() {
        return externalMatchId;
    }

    public void setExternalMatchId(String externalMatchId) {
        this.externalMatchId = externalMatchId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }
}
