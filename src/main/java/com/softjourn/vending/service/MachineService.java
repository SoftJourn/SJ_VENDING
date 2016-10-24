package com.softjourn.vending.service;


import com.softjourn.vending.dto.TransactionDTO;
import com.softjourn.vending.entity.VendingMachine;
import com.softjourn.vending.exceptions.VendingProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class MachineService {

    private VendingService vendingService;
    private RestTemplate template;

    public MachineService(VendingService vendingService, RestTemplate template) {
        this.vendingService = vendingService;
        this.template = template;
    }

    @Autowired
    public MachineService(VendingService vendingService) {
        this.vendingService = vendingService;
        this.template = new RestTemplate();
    }

    public void buy(Integer machineId, String fieldInternalId) {
        try {
            Optional.ofNullable(vendingService.get(machineId))
                    .map(VendingMachine::getUrl)
                    .map(url -> post(url, fieldInternalId))
                    .ifPresent((result) -> {
                        if (result != 200) throw new RuntimeException("Error response from server \"" + result + "\".");
                    });
        } catch (Exception e) {
            throw new VendingProcessingException("Error occurred while processing vending request. " + e.getMessage(), e);
        }

    }

    private int post(String url, String fieldInternalId) {
        ResponseEntity<TransactionDTO> response =  template.exchange(url,
                HttpMethod.POST,
                new HttpEntity<>(fieldInternalId),
                TransactionDTO.class);
        return response.getStatusCode().value();
    }


}
