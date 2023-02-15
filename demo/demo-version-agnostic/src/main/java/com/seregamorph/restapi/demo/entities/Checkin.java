package com.seregamorph.restapi.demo.entities;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "checkin")
@Getter
@Setter
@FieldNameConstants
@EntityListeners(AuditingEntityListener.class)
public class Checkin {

    @EmbeddedId
    private CheckinId id;

    @Column(name = "message")
    @Lob
    private String message;

    @ManyToOne
    @JoinColumn(name = "person_id", insertable = false, updatable = false)
    @NotNull
    private Person person;

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Instant createdDate;

    @Column(name = "last_modified_date", nullable = false)
    @LastModifiedDate
    private Instant lastModifiedDate;
}
