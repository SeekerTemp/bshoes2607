package com.vn.test.bshoes.dto;

import lombok.Data;

/**
 * One captured event from the frontend ring-buffer logger
 * (frontend/src/utils/logger.js), shipped to the backend so it can be
 * persisted into the same rolling log file as backend events.
 */
@Data
public class ClientLogEntryDto {
    private String t;
    private String level;
    private String category;
    private String message;
    private String route;
    private String user;
    private String detail;
}
