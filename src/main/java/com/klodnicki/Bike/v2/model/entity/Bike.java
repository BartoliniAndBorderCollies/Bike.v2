package com.klodnicki.Bike.v2.model.entity;

import com.klodnicki.Bike.v2.model.BikeType;
import com.klodnicki.Bike.v2.model.GpsCoordinates;
import com.klodnicki.Bike.v2.model.RentableVehicle;
import com.klodnicki.Bike.v2.model.RentalAction;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
//@DiscriminatorValue("bike")
public class Bike extends RentableVehicle implements RentalAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private BikeType bikeType;

    public Bike(String serialNumber, boolean isRented, LocalDateTime rentalStartTime, LocalDateTime rentalEndTime,
                double amountToBePaid, GpsCoordinates gpsCoordinates, Long id, BikeType bikeType) {
        super(serialNumber, isRented, rentalStartTime, rentalEndTime, amountToBePaid, gpsCoordinates);
        this.id = id;
        this.bikeType = bikeType;
    }

    public Bike() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BikeType getBikeType() {
        return bikeType;
    }

    public void setBikeType(BikeType bikeType) {
        this.bikeType = bikeType;
    }

    @Override
    public void rent() {

    }

    @Override
    public void giveBack() {

    }
}
