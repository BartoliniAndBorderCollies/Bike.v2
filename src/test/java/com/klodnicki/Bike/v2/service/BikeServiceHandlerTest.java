package com.klodnicki.Bike.v2.service;


import com.klodnicki.Bike.v2.DTO.bike.BikeForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.bike.BikeRequestDTO;
import com.klodnicki.Bike.v2.DTO.station.StationForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.user.UserForAdminResponseDTO;
import com.klodnicki.Bike.v2.model.BikeType;
import com.klodnicki.Bike.v2.model.GpsCoordinates;
import com.klodnicki.Bike.v2.model.entity.Bike;
import com.klodnicki.Bike.v2.model.entity.ChargingStation;
import com.klodnicki.Bike.v2.model.entity.Rent;
import com.klodnicki.Bike.v2.model.entity.User;
import com.klodnicki.Bike.v2.repository.BikeRepository;
import com.klodnicki.Bike.v2.repository.ChargingStationRepository;
import com.klodnicki.Bike.v2.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BikeServiceHandlerTest {

    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private ChargingStationRepository chargingStationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BikeServiceHandler bikeServiceHandler;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void add_ShouldAddToDatabase_WhenGivenCorrectArguments() {
        //given
        bikeRepository.deleteAll();
        BikeRequestDTO bikeRequestDTO = new BikeRequestDTO();
        bikeServiceHandler.add(bikeRequestDTO);
        Long expected = 1L;

        //when
        Long actual = bikeRepository.count();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void add_ShouldReturnBikeForAdminResponseDTO_WhenGivenBikeRequestDTO() {
        //given
        bikeRepository.deleteAll();
        BikeRequestDTO bikeRequestDTO = new BikeRequestDTO();
        BikeForAdminResponseDTO expected = modelMapper.map(bikeRequestDTO, BikeForAdminResponseDTO.class);

        //when
        BikeForAdminResponseDTO actual = bikeServiceHandler.add(bikeRequestDTO);

        //then
        assertNotNull(actual.getId());  // check that ID is not null
        expected.setId(actual.getId());  // set expected ID to actual ID
        assertEquals(expected, actual);
    }

    @Test
    public void findAll_ShouldReturnListOfBikeForAdminResponseDTO_WhenBikeExistInDatabase() {
        //given
        bikeRepository.deleteAll();
        ArrayList<BikeForAdminResponseDTO> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Bike bike = new Bike();
            bikeRepository.save(bike);
            BikeForAdminResponseDTO bikeDTO = modelMapper.map(bike, BikeForAdminResponseDTO.class);
            list.add(bikeDTO);
        }

        //when
        List<BikeForAdminResponseDTO> actual = bikeServiceHandler.findAll();

        //then
        assertEquals(list, actual);
    }

    @Test
    public void findById_ShouldReturnBikeForAdminResponseDTO_WhenBikeExistsInDatabase() {
        //given
        bikeRepository.deleteAll();
        Bike bike = new Bike();
        bikeRepository.save(bike);

        BikeForAdminResponseDTO expected = modelMapper.map(bike, BikeForAdminResponseDTO.class);

        //when
        BikeForAdminResponseDTO actual = bikeServiceHandler.findById(bike.getId());

        //then
        assertEquals(expected, actual);
    }


    @Test
    public void update_ShouldReturnUpdatedBikeForAdminResponseDTO_WhenGivenCorrectIdAndBikeRequestDTO() {
        //given
        bikeRepository.deleteAll();
        Bike bike = new Bike();
        bike.setRent(new Rent());

        ChargingStation chargingStation = new ChargingStation();
        chargingStationRepository.save(chargingStation);

        User user = new User();
        userRepository.save(user);

        bike.setId(1L);
        bike.setSerialNumber("123");
        bike.setRented(true);
        bike.setBikeType(BikeType.ELECTRIC);
        bike.getRent().setAmountToBePaid(100.00);
        bike.setGpsCoordinates(new GpsCoordinates("50N", "40E"));
        bike.setUser(user);
        bike.setChargingStation(chargingStation);

        bikeRepository.save(bike);

        BikeForAdminResponseDTO expected = modelMapper.map(bike, BikeForAdminResponseDTO.class);

        BikeRequestDTO bikeRequestDTO = new BikeRequestDTO(2L, "123", true, BikeType.ELECTRIC,
                100.00, new GpsCoordinates("50N", "40E"), new UserForAdminResponseDTO(),
                new StationForAdminResponseDTO());

        //when
        BikeForAdminResponseDTO actual = bikeServiceHandler.update(1L, bikeRequestDTO);

        //then
        assertEquals(expected, actual);
    }
}