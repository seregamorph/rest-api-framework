package com.seregamorph.restapi.demo.services;

import com.seregamorph.restapi.demo.entities.Checkin;
import com.seregamorph.restapi.demo.entities.CheckinId;
import com.seregamorph.restapi.demo.repositories.CheckinRepository;
import com.seregamorph.restapi.exceptions.NotFoundException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckinService {

    private final CheckinRepository checkinRepository;
    private final PersonService personService;

    public Checkin getOne(long personId, LocalDate checkinDate) {
        return checkinRepository.findById(new CheckinId(personId, checkinDate))
                .orElseThrow(() -> new NotFoundException(Checkin.class, "person", personId, "date", checkinDate));
    }

    public Checkin replace(long personId, LocalDate checkinDate, Checkin data) {
        CheckinId id = new CheckinId(personId, checkinDate);
        Checkin checkin = checkinRepository.findById(id)
                .orElse(new Checkin().setId(id))
                .setMessage(data.getMessage());
        checkin.setPerson(personService.getOne(personId));
        return checkinRepository.save(checkin);
    }
}
