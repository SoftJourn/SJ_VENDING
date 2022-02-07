package com.softjourn.vending.service;

import com.softjourn.vending.exceptions.VendingProcessingException;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"dev", "test"})
public class MachineServiceMock implements MachineService {

  @Override
  public void buy(Integer machineId, String fieldInternalId) {
    if (new Random().nextBoolean()) {
      log.info("sending request to buy for machine " + machineId + " field " + fieldInternalId);
    } else {
      throw new VendingProcessingException("Error occurred while processing vending request. ");
    }
  }

  @Override
  public void resetEngines(Integer machineId) {
    log.info("sending request to reset for machine " + machineId);
  }
}
