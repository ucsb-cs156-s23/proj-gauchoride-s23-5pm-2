package edu.ucsb.cs156.gauchoride.entities;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.AccessLevel;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.persistence.JoinColumn;
import javax.persistence.Index;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity(name = "driverchats")
@Table(name = "driverchats",indexes = @Index(name = "ts_index", columnList = "timeStamp DESC"))
public class DriverChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private LocalDateTime timeStamp;

    @NotNull
    private String messageContent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;
}
