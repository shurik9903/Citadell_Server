package org.example.model.rabbit;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IRabbit {
    Connection getConnection() throws IOException, TimeoutException;
}
