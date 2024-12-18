package com.developer.homestayersbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LightweightProperty {

    private String rentalType;
    private String stayType;
    private Long id;
    private String listingType;
    private PriceDto price;
    private String title;
    private List<ReviewDto> reviews;
    private List<PhotoDto> photos;
}
