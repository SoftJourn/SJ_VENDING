package com.softjourn.vending.controller;

import com.softjourn.vending.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('INVENTORY','SUPER_ADMIN')")
@RequestMapping(value = "/v1/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping
  public ResponseEntity<?> getDashboard() {
    return new ResponseEntity<>(dashboardService.getDashboard(), HttpStatus.OK);
  }
}
