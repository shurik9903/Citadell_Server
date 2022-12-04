package org.example.model.analysis;

import jakarta.ws.rs.core.Response;

public interface IAnalysis {
    Response loadAnalysis(String docid);
}
