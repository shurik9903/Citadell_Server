package org.example.model.report;

import jakarta.ws.rs.core.Response;

public interface IReport {
    Response getReport(String fileName, String row, String userLogin);
}
