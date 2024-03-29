package com.example.demorestapi.events;

import com.example.demorestapi.accounts.Account;
import com.example.demorestapi.accounts.AccountSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Builder
public class Event {

    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime beginEnrollmentDateTime;
    private LocalDateTime closeEnrollmentDateTime;
    private LocalDateTime beginEventDateTime;
    private LocalDateTime endEventDateTime;
    private String location; // (optional)
    private int basePrice;
    private int maxPrice;
    private  int limitOfEnrollment;
    private boolean offline;
    private boolean free;
    @Enumerated(EnumType.STRING) @Builder.Default
    private EventStatus eventStatus = EventStatus.DRAFT;
    @ManyToOne
    @JsonSerialize(using = AccountSerializer.class)
    private Account manager;

    public void update() {

        //update free
        if (this.basePrice == 0 && this.maxPrice == 0) {
            this.free = true;
        } else {
            this.free = false;
        }

        //update offline
        if (this.location == null || this.location.trim().isEmpty()){
            this.offline = false;
        } else {
            this.offline = true;
        }
    }

}
