package org.example.model.ML.predict;

import jakarta.ws.rs.core.Response;

public interface IPredict {
    Response getPredictStatus(String uuid);

    Response getPredictResult(String uuid);

    Response setPredict(String json);
}
