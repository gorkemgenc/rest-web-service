package com.github.wallet.restwebservice.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Getter
@Setter
@Entity
@Table(name="process_type")
@EntityListeners(AuditingEntityListener.class)
public class Type {

    @Id
    @NotNull
    @Column(name = "id", unique = true)
    @GeneratedValue()
    private int id;

    @Column(name = "description")
    private String description;

    public Type(){}

    public Type(int id,String description) {
        this.id = id;
        this.description = description;
    }
}
