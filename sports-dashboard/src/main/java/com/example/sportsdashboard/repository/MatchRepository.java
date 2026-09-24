package com.example.sportsdashboard.repository;

import com.example.sportsdashboard.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findAllByOrderByDateDesc();

    Optional<Match> findByExternalMatchId(String externalMatchId);
}
