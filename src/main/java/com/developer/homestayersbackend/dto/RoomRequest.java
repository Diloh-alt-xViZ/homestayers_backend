package com.developer.homestayersbackend.dto;

import com.developer.homestayersbackend.entity.Amenity;
import com.developer.homestayersbackend.entity.Pricing;
import com.developer.homestayersbackend.entity.Services;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {

    private Long propertyId;
    private Long roomId;
    private String title;
    private String description;
    private String roomType;
    private List<PhotoDto> photos;
    private List<String> amenities;
    private List<String> services;
    private PriceDto price;
    private List<AttachmentTypeDto> attachments;
    @JsonIgnore(value = true)
    private List<String> bookedDates;
}
