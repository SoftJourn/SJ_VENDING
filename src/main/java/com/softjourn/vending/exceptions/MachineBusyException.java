package com.softjourn.vending.exceptions;

public class MachineBusyException extends RuntimeException {

  public MachineBusyException(Integer machineId) {
    super(String.format("Machine with ID %d is busy at the moment", machineId));
  }
}
