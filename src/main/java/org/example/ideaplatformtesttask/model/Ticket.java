package org.example.ideaplatformtesttask.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Ticket {
    private String origin;
    private String destination;
    private String departure_date;
    private String departure_time;
    private String arrival_date;
    private String arrival_time;
    private String carrier;
    private int price;
}
