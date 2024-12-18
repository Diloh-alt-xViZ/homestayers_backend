package com.developer.homestayersbackend.controller;


import com.developer.homestayersbackend.dto.PhotoDto;
import com.developer.homestayersbackend.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HostListingDto {

    private List<PhotoDto> photos;
    private Location location;
    private Long id;
    private String title;
    private String approvalStatus;
    private Date startDate;
    private String listingType;
}
