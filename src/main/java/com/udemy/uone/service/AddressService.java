package com.udemy.uone.service;

import com.udemy.uone.shareddto.AddressDto;
import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);
    AddressDto getAddress(String addressId);
}
