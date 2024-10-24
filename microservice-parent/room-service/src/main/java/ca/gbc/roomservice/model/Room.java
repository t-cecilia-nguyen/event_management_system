package ca.gbc.roomservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="t_rooms")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    private int capacity;
    private String features;
    private boolean availability;
}
