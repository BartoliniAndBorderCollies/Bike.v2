package com.klodnicki.Bike.v2.rest.controller;

import com.klodnicki.Bike.v2.DTO.bike.BikeForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.bike.BikeRequestDTO;
import com.klodnicki.Bike.v2.DTO.bike.ListBikesForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.station.StationForAdminResponseDTO;
import com.klodnicki.Bike.v2.DTO.user.UserForAdminResponseDTO;
import com.klodnicki.Bike.v2.model.BikeType;
import com.klodnicki.Bike.v2.model.GpsCoordinates;
import com.klodnicki.Bike.v2.model.entity.Bike;
import com.klodnicki.Bike.v2.model.entity.ChargingStation;
import com.klodnicki.Bike.v2.model.entity.User;
import com.klodnicki.Bike.v2.repository.BikeRepository;
import com.klodnicki.Bike.v2.repository.ChargingStationRepository;
import com.klodnicki.Bike.v2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
class AdminBikeControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ChargingStationRepository chargingStationRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private UserRepository userRepository;
    private ChargingStation chargingStation;
    private Bike bike;

    @BeforeEach
    void setUp() {
        chargingStation = new ChargingStation();
        chargingStationRepository.save(chargingStation);
        bike = new Bike(null, BikeType.ELECTRIC, null, null, chargingStation);
        bikeRepository.save(bike);


    }

    @Test
    void addBike_ShouldAddBikeToDatabaseAndReturnBikeForAdminResponseDTO_WhenBikeRequestDTOIsProvided() {
        StationForAdminResponseDTO stationDTO = modelMapper.map(chargingStation, StationForAdminResponseDTO.class);

        BikeRequestDTO bikeRequestDTO = new BikeRequestDTO(1L, "test serialNumber", false,
                BikeType.ELECTRIC, 0, new GpsCoordinates("10N", "5E"),
                null, stationDTO);

        webTestClient.post()
                .uri("/api/admin/bikes/add")
                .bodyValue(bikeRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BikeForAdminResponseDTO.class)
                .consumeWith(response -> {
                    BikeForAdminResponseDTO bikeDTO = response.getResponseBody();
                    assertNotNull(bikeDTO);
                    assertEquals("test serialNumber", bikeDTO.getSerialNumber());
                    assertFalse(bikeDTO.isRented());
                    assertEquals(BikeType.ELECTRIC, bikeDTO.getBikeType());
                    assertEquals(0, bikeDTO.getAmountToBePaid());
                    assertEquals(new GpsCoordinates("10N", "5E"), bikeDTO.getGpsCoordinates());
                    assertNull(bikeDTO.getUser());
                    assertEquals(stationDTO, bikeDTO.getChargingStation());
                });
    }

    @Test
    void findBikeById_ShouldReturnBikeForAdminResponseDTO_WhenBikeIdIsProvidedAndBikeExistInDatabase() {
        StationForAdminResponseDTO stationDTO = modelMapper.map(chargingStation, StationForAdminResponseDTO.class);

        webTestClient.get()

                .uri("/api/admin/bikes/" + bike.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BikeForAdminResponseDTO.class)
                .consumeWith(response -> {
                    BikeForAdminResponseDTO bikeDTO = response.getResponseBody();
                    assertNotNull(bikeDTO);
                    assertEquals(bike.getId(), bikeDTO.getId());
                    assertEquals(BikeType.ELECTRIC, bikeDTO.getBikeType());
                    assertNull(bikeDTO.getRentalStartTime());
                    assertNull(bikeDTO.getUser());
                    assertEquals(stationDTO, bikeDTO.getChargingStation());
                });
    }

    @Test
    void findAllBikes_ShouldReturnListOfBikesForAdminResponseDTO_WhenBikesExistInDatabase() {
        webTestClient.get()

                .uri("/api/admin/bikes")
                // tutaj możesz umieścić headery do tego URI powyżej
                // albo np. cookies
                .exchange()
                .expectStatus().isOk()
                .expectBody(ListBikesForAdminResponseDTO.class)
                .consumeWith(response -> {
                            ListBikesForAdminResponseDTO bikes = response.getResponseBody();
                            assertNotNull(bikes);
                            assertFalse(bikes.getBikesListDTOs().isEmpty());
                            // the rest of asserts
                        }
                );
    }

    @Test
    void deleteBikeById_ShouldDeleteBikeInDatabase_WhenIdProvided() {
        //Arrange
        // I have to set charging station to null, otherwise I'm not able to delete bike because it holds foreign key
        // of charging station
        bike.setChargingStation(null);
        bikeRepository.save(bike);

        //Act
        webTestClient.delete()
                .uri("/api/admin/bikes/" + bike.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    //after deleting the bike, I try to retrieve it from the database again using
                    // bikeRepository.findById(id). This returns an Optional<Bike>
                    Optional<Bike> deletedBike = bikeRepository.findById(bike.getId());
                    // If the bike was successfully deleted, this Optional should be empty, so I assert that
                    //deletedBike.isEmpty() is true
                    assertTrue(deletedBike.isEmpty());
                });
    }

    @Test
    void updateBikeById_ShouldUpdateBike_WhenBikeExistInDatabaseAndBikeIdAndBikeRequestDTOIsGiven() {
        //Arrange
        User user = new User();
        userRepository.save(user);
        UserForAdminResponseDTO userDTO = modelMapper.map(user, UserForAdminResponseDTO.class);

        BikeRequestDTO bikeRequestDTO = new BikeRequestDTO(bike.getId(), "updated serial number",
                true, BikeType.TRADITIONAL, 0, new GpsCoordinates("updated 100N", "updated 50W"),
                userDTO, null);

        //I must set station to null and bike user to user and save it. Otherwise, bike contains foreign key of station and user,
        // and I'm not able to update it.
        bike.setChargingStation(null);
        bike.setUser(user);
        bikeRepository.save(bike);

        //Act
        webTestClient.put()
                .uri("/api/admin/bikes/" + bike.getId())
                .bodyValue(bikeRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BikeForAdminResponseDTO.class)
                .consumeWith(response -> {
                    BikeForAdminResponseDTO actualUpdatedBike = response.getResponseBody();
                    assert actualUpdatedBike != null;
                    assertEquals(bikeRequestDTO.getId(), actualUpdatedBike.getId());
                    assertEquals(bikeRequestDTO.getSerialNumber(), actualUpdatedBike.getSerialNumber());
                    assertEquals(bikeRequestDTO.isRented(), actualUpdatedBike.isRented());
                    assertEquals(bikeRequestDTO.getBikeType(), actualUpdatedBike.getBikeType());
                    assertEquals(bikeRequestDTO.getAmountToBePaid(), actualUpdatedBike.getAmountToBePaid());
                    assertEquals(bikeRequestDTO.getGpsCoordinates(), actualUpdatedBike.getGpsCoordinates());
                    assertEquals(bikeRequestDTO.getUser(), actualUpdatedBike.getUser());
                    assertNull(actualUpdatedBike.getChargingStation());
                });
    }
}