package com.densoft.springcusomerscrm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class CustomExceptionHandler {
    //add exception for customer not found exception
    @ExceptionHandler
    public ResponseEntity<CustomerErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException customerNotFoundException) {
        CustomerErrorResponse customerErrorResponse = new CustomerErrorResponse();
        customerErrorResponse.setStatus(HttpStatus.NOT_FOUND.value());
        customerErrorResponse.setMessage(customerNotFoundException.getMessage());
        customerErrorResponse.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(customerErrorResponse, HttpStatus.NOT_FOUND);
    }

    //general exception handler
    @ExceptionHandler
    public ResponseEntity<CustomerErrorResponse> generalException(Exception e) {
        CustomerErrorResponse customerErrorResponse = new CustomerErrorResponse();
        customerErrorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        customerErrorResponse.setMessage(e.getMessage());
        customerErrorResponse.setTimeStamp(System.currentTimeMillis());
        return new ResponseEntity<>(customerErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
