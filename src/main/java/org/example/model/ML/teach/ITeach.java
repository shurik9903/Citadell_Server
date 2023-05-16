package org.example.model.ML.teach;

import jakarta.ws.rs.core.Response;

public interface ITeach {
    Response getTeachStatus(String uuid);

    Response getTeachResult(String uuid);

    Response setTeach(String json);
}
