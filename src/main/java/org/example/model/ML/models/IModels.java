package org.example.model.ML.models;

import jakarta.ws.rs.core.Response;

public interface IModels {
    Response getModels();

    Response setModels(String data);

    Response deleteModels(String id);
}
