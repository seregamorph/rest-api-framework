package com.seregamorph.restapi.demo.entities;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Embeddable
@Getter
@Setter
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
public class CheckinId implements Serializable {

    private static final long serialVersionUID = 3539951481407147979L;

    @Column(name = "person_id")
    private long personId;

    @Column(name = "checkin_date")
    private LocalDate checkinDate;
}
