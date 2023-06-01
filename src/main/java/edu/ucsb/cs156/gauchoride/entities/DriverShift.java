package edu.ucsb.cs156.gauchoride.entities;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Entity(name = "shifts")
public class DriverShift {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private Weekday weekday;

  @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
  private LocalDateTime startShiftTime;

  @JsonFormat(pattern = "HH:mm:ss", shape = JsonFormat.Shape.STRING)
  private LocalDateTime endShiftTime;

  @ManyToOne
  private User driver;

  @ManyToOne
  private User backupDriver;

  public enum Weekday {
    Monday, Tuesday, Wednesday, Thursday, Friday;
  }
}


