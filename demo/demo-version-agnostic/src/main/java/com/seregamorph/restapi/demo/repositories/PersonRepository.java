package com.seregamorph.restapi.demo.repositories;

import com.seregamorph.restapi.demo.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

}
