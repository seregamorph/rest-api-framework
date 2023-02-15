package com.seregamorph.restapi.demo.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "person")
@Getter
@Setter
@FieldNameConstants
@EntityListeners(AuditingEntityListener.class)
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Min(1900)
    @Column(name = "year_of_birth")
    private Integer yearOfBirth;

    @Email
    @Column(name = "email_address")
    private String emailAddress;

    @Column(name = "activation_date")
    private Instant activationDate;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "manager")
    private List<Team> managedTeams = new ArrayList<>();

    @Column(name = "created_date", nullable = false)
    @CreatedDate
    private Instant createdDate;

    @Column(name = "last_modified_date", nullable = false)
    @LastModifiedDate
    private Instant lastModifiedDate;
}
