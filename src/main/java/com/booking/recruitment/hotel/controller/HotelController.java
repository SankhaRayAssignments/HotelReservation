package com.booking.recruitment.hotel.controller;

import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {
  private final HotelService hotelService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired
  public HotelController(HotelService hotelService) {
    this.hotelService = hotelService;
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<Hotel> getAllHotels() {
    return hotelService.getAllHotels();
  }

  @PostMapping(
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public Hotel createHotel(@RequestBody Hotel hotel) {
    return hotelService.createNewHotel(hotel);
  }

  @GetMapping("{id}")
  public ResponseEntity<String> getHotelById(@PathVariable Long id) throws JsonProcessingException {

    Hotel hotel = hotelService.getHotelById(id);
    if (hotel != null) {
      String res = objectMapper.writeValueAsString(hotel);
      return ResponseEntity.status(HttpStatus.OK)
              .body(res);
    } else {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
              .body("Hotel not found with id: " + id);
    }
  }

  @GetMapping("/search/{cityId}")
  public ResponseEntity<String> getNearestHotels
          (@PathVariable Long cityId,
                  @RequestParam(value = "sortBy")
                          String sortBy) throws JsonProcessingException {

    List<Hotel> hotels = hotelService.getNearestHotelsByDistance(cityId);
    if (hotels != null) {
      String res = objectMapper.writeValueAsString(hotels);
      return ResponseEntity.status(HttpStatus.OK)
              .body(res);
    } else {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
              .body("Hotel not found with");
    }
  }

  @DeleteMapping("{id}")
  public ResponseEntity<String>
  deleteById(@PathVariable Long id) throws JsonProcessingException {

    Hotel hotel = hotelService.deleteHotelById(id);
    if(hotel != null) {
      return ResponseEntity.status(HttpStatus.ACCEPTED)
              .body("Deleted Hotel with id: " + id);
    } else {
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
              .body("Hotel not found with id:" + id);
    }
  }

}
