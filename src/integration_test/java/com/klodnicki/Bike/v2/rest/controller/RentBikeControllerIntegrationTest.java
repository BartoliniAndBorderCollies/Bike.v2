package com.klodnicki.Bike.v2.rest.controller;

import com.klodnicki.Bike.v2.DTO.bike.BikeForNormalUserResponseDTO;
import com.klodnicki.Bike.v2.DTO.bike.ListBikesForNormalUserResponseDTO;
import com.klodnicki.Bike.v2.DTO.rent.RentResponseDTO;
import com.klodnicki.Bike.v2.model.BikeType;
import com.klodnicki.Bike.v2.model.RentRequest;
import com.klodnicki.Bike.v2.model.entity.Bike;
import com.klodnicki.Bike.v2.model.entity.ChargingStation;
import com.klodnicki.Bike.v2.model.entity.User;
import com.klodnicki.Bike.v2.repository.BikeRepository;
import com.klodnicki.Bike.v2.repository.ChargingStationRepository;
import com.klodnicki.Bike.v2.repository.RentRepository;
import com.klodnicki.Bike.v2.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.properties")
class RentBikeControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ChargingStationRepository chargingStationRepository;
    @Autowired
    private BikeRepository bikeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RentRepository rentRepository;
    @Autowired
    private ModelMapper modelMapper;
    private Bike bike;
    private ChargingStation chargingStation;
    private User user;

    @BeforeEach
    public void setUp() {
        chargingStation = new ChargingStation(null, "station name", "station address", "station city",
                100, new ArrayList<>());
        chargingStationRepository.save(chargingStation);

        bike = new Bike(null, BikeType.ELECTRIC, null, null, chargingStation);
        bikeRepository.save(bike);

        user = new User(null, "user name", "user phone nr", "user email",
                12345, true, "user", 100.00, null, null);
        userRepository.save(user);
    }

    @Test
    public void findAvailableBikes_ShouldReturnOneObjectCalledListBikesForNormalUserResponseDTOAndMustBeNotRented_WhenBikesExistInDatabase() {
        bikeRepository.deleteAll();

        Bike bike1NotRented = new Bike(1L, BikeType.ELECTRIC, null, null, chargingStation);
        Bike bike2NotRented = new Bike(2L, BikeType.TRADITIONAL, null, null, chargingStation);
        Bike bike3Rented = new Bike(3L, BikeType.ELECTRIC, null, null, chargingStation);
        bike1NotRented.setRented(false);
        bike2NotRented.setRented(false);
        bike3Rented.setRented(true);

        bikeRepository.save(bike1NotRented);
        bikeRepository.save(bike2NotRented);
        bikeRepository.save(bike3Rented);

        List<BikeForNormalUserResponseDTO> dtoList = new ArrayList<>();
        List<Bike> bikeList = new ArrayList<>();
        bikeList.add(bike1NotRented);
        bikeList.add(bike2NotRented);

        for (Bike bike : bikeList) {
            BikeForNormalUserResponseDTO bikeDTO = BikeForNormalUserResponseDTO.builder()
                    .id(bike.getId())
                    .serialNumber(bike.getSerialNumber())
                    .isRented(bike.isRented())
                    .bikeType(bike.getBikeType())
                    .build();
            dtoList.add(bikeDTO);
        }

        ListBikesForNormalUserResponseDTO expected = new ListBikesForNormalUserResponseDTO(dtoList);

        webTestClient.get()
                .uri("/api/bikes")
                .exchange()
                .expectStatus().isOk()
                .expectBody(ListBikesForNormalUserResponseDTO.class)
                .consumeWith(response -> {
                    ListBikesForNormalUserResponseDTO responseListDTO = response.getResponseBody();
                    assertNotNull(responseListDTO);
                    assertFalse(responseListDTO.getListOfBikesDTOs().isEmpty());
                    assertIterableEquals(expected.getListOfBikesDTOs(), responseListDTO.getListOfBikesDTOs());
                });
    }

    @Test
    public void findBikeForNormalUserById_ShouldReturnBikeForNormalUserResponseDTO_WhenBikeExistsInDatabaseAndBikeIdIsGiven(){
        BikeForNormalUserResponseDTO expected = modelMapper.map(bike, BikeForNormalUserResponseDTO.class);

        webTestClient.get()
                .uri("/api/bikes/" + bike.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BikeForNormalUserResponseDTO.class)
                .consumeWith(response -> {
                    BikeForNormalUserResponseDTO  bikeResponseDTO = response.getResponseBody();
                    assertNotNull(bikeResponseDTO);
                    assertEquals(expected, bikeResponseDTO);
                });
    }

    @Test
    public void rentBike_ShouldReturnRentResponseDTOAndSaveRentInDatabase_WhenRentRequestIsGiven() {
        rentRepository.deleteAll();
        RentRequest rentRequest = new RentRequest(null, user.getId(), bike.getId(), 10);

        webTestClient.post()
                .uri("/api/rentals/add")
                .bodyValue(rentRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RentResponseDTO.class)
                .consumeWith(response -> {
                    RentResponseDTO responseDTO = response.getResponseBody();
                    assertNotNull(responseDTO);
                    assertTrue(rentRepository.count()>0);
                    assertEquals(rentRequest.getDaysOfRent(), responseDTO.getDaysOfRent());
                    assertEquals(user.getName(), responseDTO.getUserForNormalUserResponseDTO().getName());
                    assertEquals(user.getRole(), responseDTO.getUserForNormalUserResponseDTO().getRole());
                    assertEquals(bike.getSerialNumber(), responseDTO.getBikeForNormalUserResponseDTO().getSerialNumber());
                    assertEquals(bike.getBikeType(), responseDTO.getBikeForNormalUserResponseDTO().getBikeType());
                });
    }
}