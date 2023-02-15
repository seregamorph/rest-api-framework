package com.seregamorph.restapi.demo.repositories;

import com.seregamorph.restapi.demo.entities.Checkin;
import com.seregamorph.restapi.demo.entities.CheckinId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckinRepository extends JpaRepository<Checkin, CheckinId> {

}
