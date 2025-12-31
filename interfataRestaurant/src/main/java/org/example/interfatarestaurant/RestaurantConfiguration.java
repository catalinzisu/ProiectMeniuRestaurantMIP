package org.example.interfatarestaurant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RestaurantConfiguration {
    public String numeRestaurant;
    public double tvaProcent;

    // Setari noi pentru oferte
    public boolean happyHourActive;
    public boolean mealDealActive;
    public boolean partyPackActive;

    // Constructor gol necesar pentru Jackson
    public RestaurantConfiguration() {}

    public RestaurantConfiguration(String nume, double tva) {
        this.numeRestaurant = nume;
        this.tvaProcent = tva;
    }
}