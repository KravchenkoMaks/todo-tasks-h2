package com.mk.todotasksh2.model;

import jakarta.persistence.*;
import lombok.*;
import com.mk.todotasksh2.util.TaskStateConverter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(of ="id")
@Builder
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;

    @Column
    private LocalDate deadline;

    @Column
    @Convert(converter = TaskStateConverter.class)
    private TaskState state;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
