package com.vn.test.bshoes.dto;

import lombok.Data;

import java.util.List;

/** Batch of frontend log entries posted to /api/logs/client. */
@Data
public class ClientLogBatchDto {
    private List<ClientLogEntryDto> logs;
}
