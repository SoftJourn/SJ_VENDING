package com.softjourn.vending.utils;

public interface Constants {

    // Sql error codes
    Integer SQL_DUPLICATE_ENTRY = 1062;
    Integer SQL_CANNOT_DELETE_OR_UPDATE_PARENT_ROW = 1451;

    // Image files
    int IMAGE_FILE_MAX_SIZE = 1024 * 512;
    int IMAGE_DIMENSIONS_MAX_WIDTH = 1024;
    int IMAGE_DIMENSIONS_MAX_HEIGHT = 1024;

    // Dates
    Integer LAST_WEEK = 7;
    Integer LAST_MONTH = 30;

}
