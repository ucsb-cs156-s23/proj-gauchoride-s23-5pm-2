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
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
//(access = AccessLevel.PROTECTED)
@Builder
@Entity(name = "driverShifts")
public class DriverShift {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  @ManyToOne
  private User driver;

  @ManyToOne
  private User backupDriver;

  private Weekday weekday;

  private LocalTime startTime;

  private LocalTime endTime;

  public enum Weekday {
    Monday, Tuesday, Wednesday, Thursday, Friday;
  }
}


