package com.softjourn.vending.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "test"})
public class MachineServiceMock implements MachineService {
    @Override
    public void buy(Integer machineId, String fieldInternalId) {
        log.info("sending request to buy from machine " + machineId + " field " + fieldInternalId);
    }


}
