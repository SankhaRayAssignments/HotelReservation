package com.booking.recruitment.hotel.service.impl;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class DefaultHotelService implements HotelService {
  private final HotelRepository hotelRepository;

  @Autowired
  DefaultHotelService(HotelRepository hotelRepository) {
    this.hotelRepository = hotelRepository;
  }

  @Override
  public List<Hotel> getAllHotels() {
    return hotelRepository.findAll();
  }

  @Override
  public List<Hotel> getHotelsByCity(Long cityId) {
    return hotelRepository.findAll().stream()
        .filter((hotel) -> cityId.equals(hotel.getCity().getId()))
        .collect(Collectors.toList());
  }

  @Override
  public Hotel createNewHotel(Hotel hotel) {
    if (hotel.getId() != null) {
      throw new BadRequestException("The ID must not be provided when creating a new Hotel");
    }

    return hotelRepository.save(hotel);
  }

  @Override
  public Hotel getHotelById(Long hotelId) {
    Hotel hotel = hotelRepository.findById(hotelId).filter(hotel1 -> !hotel1.isDeleted()).orElse(null);
    return hotel;
  }

  @Override
  public Hotel deleteHotelById(final Long hotelId) {
    // SoftDelete
    Optional<Hotel> optHotel = hotelRepository.findById(hotelId).filter(hotel -> !hotel.isDeleted());
    Hotel hotel = null;
    if (optHotel.isPresent()) {
      hotel = optHotel.get();
      hotel.setDeleted(true);
      hotelRepository.delete(hotel);
      hotelRepository.save(hotel);
    }
    return hotel;
  }

  @Override
  public List<Hotel> getNearestHotelsByDistance(Long cityId) {

    Comparator<Hotel> hotelCompator
            = Comparator.comparingDouble(Hotel::haversine);


    // SoftDelete
    List<Hotel> hotels = hotelRepository.findAll().stream()
            .filter(hotel -> !hotel.isDeleted())
            .filter(hotel -> hotel.getCity() != null)
            .filter(hotel -> hotel.getCity().getId() == cityId)
            .collect(Collectors.toList());



    if (hotels.size() == 0) {
      return null;
    }

    Hotel[] myArray = new Hotel[hotels.size()];
    hotels.toArray(myArray);

    Arrays.sort(myArray, hotelCompator);
    List<Hotel> result = new ArrayList<>();
    int maxCount = Math.min(hotels.size(), 3);
    for (int i = 0; i < maxCount; i++) {
      result.add(myArray[i]);
    }
    return result;

  }

}
