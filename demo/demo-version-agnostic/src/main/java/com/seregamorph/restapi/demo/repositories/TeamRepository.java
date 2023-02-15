package com.seregamorph.restapi.demo.repositories;

import com.seregamorph.restapi.demo.entities.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

}
