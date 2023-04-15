package org.example.model.messageBroker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.ws.rs.core.Response;
import org.example.model.rabbit.IRabbit;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessBrok implements IMessBrok {

    //Канал обмена exchangeDeclare (имя канала обмена, тип обмена, надежный?, автоудаление?, аргументы)
    /*

    channel.ExchangeDeclare(
        exchange: "my_exchange",
        type: "direct",
        durable: "false",
        autoDelete: "false",
        arguments: null
    );

    fanout - направляет все сообщения, отправляемые на Exchange, во все связанные с ним очереди
    direct - направит сообщение в очередь, где ключ привязки и ключ маршрутизации точно совпадают.
    * пример key:
    * error - 1 очередь
    * info, error, warning, qwerty - 2 очередь
    * Другой любой ключ - не попадет не в какую очередь
    topic - направляет сообщение в очередь, где ключ привязки совпадает с ключом маршрутизации
    * Ключ маршрутизации - это символьная строка, разделенная точкой "."
    (Мы называем каждую независимую символьную строку, разделенную точкой "." Словом),
    * например, "stock.usd.nyse", "nyse." vmw "," quick.orange.rabbit ".
    * Ключ привязки - это строка символов, разделенная точкой ".", Как ключ маршрутизации.

    * В качестве примера рассмотрим приведенную выше конфигурацию: сообщение с routingKey = ”quick.orange.rabbit”
    * будет одновременно направлено на Q1 и Q2, сообщение с routingKey = ”lazy.orange.fox” будет направлено на Q1,
    * routingKey = ”lazy.brown. Сообщение "fox" будет направлено на Q2, а сообщение routingKey = "lazy.pink.rabbit"
    * будет направлено на Q2 (оно будет доставлено на Q2 только один раз, хотя этот routingKey соответствует обоим связующим ключам Q2);
    * routingKey = "quick. Сообщения Brown.fox ”, routingKey =” orange ”,
    * routingKey =” quick.orange.male.rabbit ”будут отбрасываться, поскольку они не соответствуют ни одному из связующих ключей.
    */


    //Очереди queueDeclare (имя очереди, надежный?, эксклюзивный?, автоудаление?, аргументы)
    /*
    channel.QueueDeclare(
        queue: "my_queue",
        durable: false,
        exclusive: false,
        autoDelete: false,
        arguments: null
    );

    * queue — название очереди, которую мы хотим создать. Название должно быть уникальным и не может совпадать с системным именем очереди
    * durable — если true, то очередь будет сохранять свое состояние и восстанавливается после перезапуска сервера/брокера
    * exclusive — если true, то очередь будет разрешать подключаться только одному потребителю
    * autoDelete — если true, то очередь обретает способность автоматически удалять себя
    * arguments — необязательные аргументы.
    */

    //Привязка QueueBind (имя очереди, имя обменника, ключ, аргументы)
    /*
    channel.QueueBind(
        queue: queueName,
        exchange: "my_exchange",
        routingKey: "my_key",
        arguments: null
    );

    * queue — имя очереди
    * exchange — имя обменника
    * routingKey — ключ маршрутизации
    * arguments — необязательные аргументы
    */

    //Отправка сообщения (имя обменника, ключ, обязательный флаг, свойства, сообщение)

    /*
    channel.basicPublish(
        exchangeName,
        routingKey,
        mandatory,
        MessageProperties.PERSISTENT_TEXT_PLAIN,
        messageBodyBytes
    );

    пример
    channel.basicPublish(
    exchangeName,
    routingKey,
    new AMQP.BasicProperties.Builder()
       .contentType("text/plain")
       .deliveryMode(2)
       .priority(1)
       .userId("bob")
       .build(),
    messageBodyBytes
    );
    */

    //Подписка basicConsume (имя очереди, авто подтверждение, тэг, сообщение)
    /*
    boolean autoAck = false;
    channel.basicConsume(
        queueName,
        autoAck,
        "myConsumerTag",
        new DefaultConsumer(channel) {
             @Override
             public void handleDelivery(
                                        String consumerTag,
                                        Envelope envelope,
                                        AMQP.BasicProperties properties,
                                        byte[] body
              ) throws IOException
             {
                 String routingKey = envelope.getRoutingKey();
                 String contentType = properties.getContentType();
                 long deliveryTag = envelope.getDeliveryTag();
                 // (process the message components here ...)
                 channel.basicAck(deliveryTag, false);
             }
     });
    */


    @Inject
    IRabbit rabbit;

    //Создание канала обмена
    @Override
    public Response declare(String json){

        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> jsonData = new HashMap<>();
        jsonData = (Map<String,String>) jsonb.fromJson(json, jsonData.getClass());

        String user = jsonData.getOrDefault("user", "");
        String file_name = jsonData.getOrDefault("file_name", "");

        Connection connection = null;
        Channel channel;

        try {

            connection = rabbit.getConnection();

            channel = connection.createChannel();

            //Канал обмена - перенаправляет сообщения в очереди в зависимости от указанного типа
            //надежный, автоудаление
//            channel.exchangeDeclare("ex_user_file", "direct", true, true, null);
            channel.exchangeDeclare("ex_user_file", "direct", true);

            //Очередь сообщений (имя очереди, надежный, эксклюзивный)
            //надежный, эксклюзивный
//            channel.queueDeclare("qu_user_file", true, true, false, null);
            channel.queueDeclare("qu_user_file", true, false, false, null);

            //связывание канала обмена и очереди посредством ключа
            channel.queueBind("qu_user_file", "ex_user_file", user + file_name);

            //Отправка сообщения
//            channel.basicPublish("", "hello-world", false, null, message.getBytes(StandardCharsets.UTF_8));

            System.out.println("Declare create!");


            return Response.ok("Declare create!").build();
        } catch (Exception e){
            System.out.println("Error MessBrok: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        } finally {
            try {
                assert connection != null;
                connection.close();
            } catch (Exception e){
                System.out.println("Error MessBrok: " + e.getMessage());
            }

        }



    }

    //Отправка сообщения
    @Override
    public Response send(String json){
        Jsonb jsonb = JsonbBuilder.create();
        Map<String, String> jsonData = new HashMap<>();
        jsonData = (Map<String,String>) jsonb.fromJson(json, jsonData.getClass());

        String message  = jsonData.getOrDefault("msg", "");
        String key  = jsonData.getOrDefault("key", "");

        Connection connection = null;
        Channel channel;

        try {

            connection = rabbit.getConnection();

            channel = connection.createChannel();

            //Отправка сообщения
            channel.basicPublish("ex_user_file", key, false, null, message.getBytes(StandardCharsets.UTF_8));

            System.out.println("Message sent!");


            return Response.ok("Message sent!").build();
        } catch (Exception e){
            System.out.println("Error MessBrok: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        } finally {
            try {
                assert connection != null;
                connection.close();
            } catch (Exception e){
                System.out.println("Error MessBrok: " + e.getMessage());
            }

        }
    }

    //Подписаться на получение сообщения
    @Override
    public Response consume(){

        Connection connection = null;
        Channel channel;

        try {

            connection = rabbit.getConnection();

            channel = connection.createChannel();

//            channel.queueDeclare("hello-world", false, false, false, null);

            //Подписка
            channel.basicConsume("qu_user_file", true, (consumerTag, message) -> {
                String getMessage = new String(message.getBody(), StandardCharsets.UTF_8);
                System.out.println("message received: " + getMessage);
                }, consumerTag -> {});

            System.out.println("Consume!");

            return Response.ok("ok").build();
        } catch (Exception e){
            System.out.println("Error MessBrok: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity("|Error: " + e.getMessage()).build();
        }
//        finally {
//            try {
//                assert connection != null;
//                connection.close();
//            } catch (Exception e){
//                System.out.println("Error MessBrok: " + e.getMessage());
//            }
//
//        }

    }

}
