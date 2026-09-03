package com.saloni.banking.controller;
import org.springframework.ui.Model; import org.springframework.web.bind.annotation.ControllerAdvice; import org.springframework.web.bind.annotation.ExceptionHandler;
@ControllerAdvice public class GlobalExceptionHandler{ @ExceptionHandler(Exception.class) public String handle(Exception e,Model m){m.addAttribute("message",e.getMessage()==null?"Something went wrong.":e.getMessage());return "error";} }
