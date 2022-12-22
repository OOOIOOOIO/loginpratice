package com.sh.loginpratice.websecurityjwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {
    private int statusValue;
    private Date date;
    private String message;
    private String request;
}
