package org.example.interfatarestaurant.model;

import jakarta.persistence.*;

/**
 * Entity pentru starea ofertelor (Happy Hour, Meal Deal, Party Pack)
 */
@Entity
@Table(name = "offer_state")
public class OfferState {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean happyHourActive = false;
    private boolean mealDealActive = false;
    private boolean partyPackActive = false;

    public OfferState() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public boolean isHappyHourActive() { return happyHourActive; }
    public void setHappyHourActive(boolean happyHourActive) { this.happyHourActive = happyHourActive; }

    public boolean isMealDealActive() { return mealDealActive; }
    public void setMealDealActive(boolean mealDealActive) { this.mealDealActive = mealDealActive; }

    public boolean isPartyPackActive() { return partyPackActive; }
    public void setPartyPackActive(boolean partyPackActive) { this.partyPackActive = partyPackActive; }
}

