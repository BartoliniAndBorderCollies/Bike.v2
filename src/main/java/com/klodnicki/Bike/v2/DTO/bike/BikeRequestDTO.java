package com.klodnicki.Bike.v2.DTO.bike;

import com.klodnicki.Bike.v2.DTO.station.StationForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.user.UserForAdminResponseDTO;
import com.klodnicki.Bike.v2.model.BikeType;
import com.klodnicki.Bike.v2.model.GpsCoordinates;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BikeRequestDTO {

    @NotNull(message = "Id must not be null")
    private Long id;
    @NotBlank(message = "Serial number must not be blank")
    private String serialNumber;
    private boolean isRented;
    @NotNull(message = "Bike type must not be null")
    private BikeType bikeType;
    @PositiveOrZero(message = "Amount to be paid must be positive or zero")
    private double amountToBePaid;
    @NotNull(message = "GPS coordinates must not be null")
    private GpsCoordinates gpsCoordinates;
    private UserForAdminResponseDTO user;
    private StationForAdminResponseDTO chargingStation;
}