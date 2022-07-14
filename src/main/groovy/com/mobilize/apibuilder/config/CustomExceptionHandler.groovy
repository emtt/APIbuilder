package com.mobilize.apibuilder.config

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
@RestController
class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {

        def response = [:]
        response.put("Date", new Date())
        response.put("message", ex.getMessage())
        response.put("details", request.getDescription(true))

        ResponseEntity responseEntity = ResponseEntity.badRequest()
                .header("Custom-Header", "error")
                .body(response)
        return responseEntity

    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        def detalles = []
        ex.bindingResult.allErrors.each {error ->
            detalles << error.defaultMessage
        }
        def response = [:]
        response.put("Date", new Date())
        response.put("message", "Validation error")
        response.put("details", detalles)
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }
}