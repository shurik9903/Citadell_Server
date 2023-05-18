package org.example.model.analysis;

import jakarta.ws.rs.core.Response;

public interface IAnalysis {
    Response startAnalysis(String json, String userLogin, String userID);

    Response getAnalysisStatus(String uuid);

    Response getAnalysisResult(String uuid, String userLogin);
}
