package com.seregamorph.restapi.demo.services;

import com.seregamorph.restapi.demo.entities.Person;
import com.seregamorph.restapi.demo.entities.Team;
import com.seregamorph.restapi.demo.repositories.PersonRepository;
import com.seregamorph.restapi.exceptions.NotFoundException;
import java.util.List;
import javax.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final TeamService teamService;

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    @Nonnull
    public Person getOne(long id) {
        return personRepository.findById(id).orElseThrow(() -> new NotFoundException(Person.class, id));
    }

    public Person create(Person person) {
        Team team = teamService.getOne(person.getTeam().getId());
        person.setTeam(team);
        team.getMembers().add(person);
        return personRepository.save(person);
    }

    public Person update(long id, Person update) {
        Person person = getOne(id);
        person.setName(update.getName());
        person.setYearOfBirth(update.getYearOfBirth());
        person.setEmailAddress(update.getEmailAddress());
        person.setActivationDate(update.getActivationDate());
        person.setTeam(teamService.getOne(update.getTeam().getId()));
        return personRepository.save(person);
    }

    public void delete(long id) {
        Person person = getOne(id);
        person.getTeam().getMembers().remove(person);
        person.getManagedTeams().forEach(team -> team.setManager(null));
        personRepository.delete(person);
    }
}
