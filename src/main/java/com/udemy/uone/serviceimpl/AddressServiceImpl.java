package com.udemy.uone.serviceimpl;

import com.udemy.uone.io.entity.AddressEntity;
import com.udemy.uone.io.entity.UserEntity;
import com.udemy.uone.repository.AddressRepository;
import com.udemy.uone.repository.UserRepository;
import com.udemy.uone.service.AddressService;
import com.udemy.uone.shareddto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {

        ModelMapper modelMapper = new ModelMapper();
        List<AddressDto> returnValue = new ArrayList<>();
        UserEntity userEntity = userRepository.findByUserId(userId);

        if(userEntity == null) return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addresses) {
            returnValue.add(modelMapper.map(addressEntity, AddressDto.class));
        }
        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {

        AddressDto returnValue = null;

        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);

        if(addressEntity!=null) {
            returnValue = new ModelMapper().map(addressEntity, AddressDto.class);
        }
        return returnValue;
    }
}
