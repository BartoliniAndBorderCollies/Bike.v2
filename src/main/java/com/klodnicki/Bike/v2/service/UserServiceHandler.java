package com.klodnicki.Bike.v2.service;

import com.klodnicki.Bike.v2.DTO.user.UserForAdminResponseDTO;
import com.klodnicki.Bike.v2.exception.NotFoundInDatabaseException;
import com.klodnicki.Bike.v2.model.entity.User;
import com.klodnicki.Bike.v2.repository.UserRepository;
import com.klodnicki.Bike.v2.service.api.UserServiceApi;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceHandler implements UserServiceApi {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserForAdminResponseDTO add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User user1 = userRepository.save(user);

        return modelMapper.map(user1, UserForAdminResponseDTO.class);
    }
    @Override
    public List<UserForAdminResponseDTO> findAll() {
        Iterable<User> users = userRepository.findAll();
        List<UserForAdminResponseDTO> listUsersDTO = new ArrayList<>();

        for (User user: users) {
            UserForAdminResponseDTO userDto = modelMapper.map(user, UserForAdminResponseDTO.class);
            listUsersDTO.add(userDto);
        }
        return listUsersDTO;
    }
    @Override
    public UserForAdminResponseDTO findById(Long id) throws NotFoundInDatabaseException {
        User user = findUserById(id);

        return modelMapper.map(user, UserForAdminResponseDTO.class);
    }
    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
    @Override
    public User findUserById(Long id) throws NotFoundInDatabaseException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundInDatabaseException(User.class));
    }

    @Override
    public ResponseEntity<?> banUser(Long id) throws NotFoundInDatabaseException {
        User user = findUserById(id);

        user.setAccountValid(false);
        userRepository.save(user);

        return new ResponseEntity<>("User banned successfully", HttpStatus.OK);
    }
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
