package edu.ucsb.cs156.gauchoride.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ride")
@Table(name= "ride")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String day;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User rider; 

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User driver; 


    private String course;
    private LocalTime timeStart;
    private LocalTime timeStop;
    private String building;
    private String room;
    private String pickUp;
}