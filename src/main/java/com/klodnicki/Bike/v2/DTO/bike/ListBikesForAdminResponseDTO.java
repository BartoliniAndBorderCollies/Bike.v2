package com.klodnicki.Bike.v2.DTO.bike;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListBikesForAdminResponseDTO {

    private List<BikeForAdminResponseDTO> bikesListDTOs;
}
