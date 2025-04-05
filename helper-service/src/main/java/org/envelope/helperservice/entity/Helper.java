package org.envelope.helperservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "helpers")
public class Helper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ColumnDefault("nextval('helpers_helper_id_seq')")
    @Column(name = "helper_id", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "tg_id", nullable = false, length = 100)
    private String tgId;

    @Size(max = 255)
    @Column(name = "firstname")
    private String firstname;

    @Size(max = 255)
    @Column(name = "lastname")
    private String lastname;

}