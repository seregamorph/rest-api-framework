package com.seregamorph.restapi.demo.services;

import com.seregamorph.restapi.demo.entities.Team;
import com.seregamorph.restapi.demo.repositories.TeamRepository;
import com.seregamorph.restapi.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public Team getOne(long id) {
        return teamRepository.findById(id).orElseThrow(() -> new NotFoundException(Team.class, id));
    }

    public Team save(Team team) {
        return teamRepository.save(team);
    }
}
