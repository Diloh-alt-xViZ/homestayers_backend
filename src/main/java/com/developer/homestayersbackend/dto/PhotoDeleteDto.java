package com.developer.homestayersbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PhotoDeleteDto {

    private String uri;
    private Long id;
}
