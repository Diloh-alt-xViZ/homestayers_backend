package com.developer.homestayersbackend.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PhoneNumberNotVerifiedException extends RuntimeException {

    public PhoneNumberNotVerifiedException(String message) {
        super(message);
    }
}
