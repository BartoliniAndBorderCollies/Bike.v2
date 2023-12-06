package com.klodnicki.Bike.v2.rest.controller;

import com.klodnicki.Bike.v2.DTO.bike.BikeForNormalUserResponseDTO;
import com.klodnicki.Bike.v2.DTO.bike.ListBikesForNormalUserResponseDTO;
import com.klodnicki.Bike.v2.DTO.rent.RentRequestDTO;
import com.klodnicki.Bike.v2.DTO.rent.RentResponseDTO;
import com.klodnicki.Bike.v2.exception.NotFoundInDatabaseException;
import com.klodnicki.Bike.v2.model.RentRequest;
import com.klodnicki.Bike.v2.service.api.RentBikeServiceApi;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RentBikeController {

    private final RentBikeServiceApi rentBikeService;


    @GetMapping("/bikes")
    public ListBikesForNormalUserResponseDTO findAvailableBikes() {
        List<BikeForNormalUserResponseDTO> bikeDTOs = rentBikeService.findAvailableBikes();

        return new ListBikesForNormalUserResponseDTO(bikeDTOs);
    }

    @GetMapping("/bikes/{id}")
    public BikeForNormalUserResponseDTO findBikeForNormalUserById(@PathVariable Long id) throws NotFoundInDatabaseException {
        return rentBikeService.findBikeForNormalUserById(id);
    }

    @PostMapping("/rentals/add")
    public RentResponseDTO rentBike(@Valid @RequestBody RentRequest rentRequest) throws NotFoundInDatabaseException {
        return rentBikeService.rent(rentRequest);
    }

    @PutMapping("/rentals/{id}")
    public RentResponseDTO updateRent(@PathVariable Long id, @Valid @RequestBody RentRequestDTO rentRequestDTO) {
        return rentBikeService.updateRent(id, rentRequestDTO);
    }

    @PutMapping("/returns/{rentId}")
    public ResponseEntity<?> returnBike(@PathVariable Long rentId, @RequestParam Long returnChargingStationId)
            throws NotFoundInDatabaseException {
        return rentBikeService.returnVehicle(rentId, returnChargingStationId);
    }
}
