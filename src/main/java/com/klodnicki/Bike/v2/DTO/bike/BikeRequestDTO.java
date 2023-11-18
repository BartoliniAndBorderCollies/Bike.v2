package com.klodnicki.Bike.v2.DTO.bike;

import com.klodnicki.Bike.v2.DTO.station.StationForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.user.UserForAdminResponseDTO;
import com.klodnicki.Bike.v2.model.BikeType;
import com.klodnicki.Bike.v2.model.GpsCoordinates;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BikeRequestDTO {

    private Long id;
    private String serialNumber;
    private boolean isRented;
    private BikeType bikeType;
    private double amountToBePaid;
    private GpsCoordinates gpsCoordinates;
    private UserForAdminResponseDTO user;
    private StationForAdminResponseDTO chargingStation;
}