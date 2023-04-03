package org.example.model.rabbit;


import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Rabbit implements IRabbit {

    private ConnectionFactory conFact;

    @PostConstruct
    public void postConnectionFactory(){
        conFact = new ConnectionFactory();
        conFact.setUsername("admin");
        conFact.setPassword("admin");
        conFact.setVirtualHost("/");
        conFact.setHost("localhost");
        conFact.setPort(5672);
    }

    @Override
    public Connection getConnection() throws IOException, TimeoutException {
            return conFact.newConnection();
    }

}
