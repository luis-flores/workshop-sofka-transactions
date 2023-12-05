package EPA.Cuenta_Bancaria_Web.handlers.bus;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "transactions-queue";
    public static final String QUEUE_NAME_2 = "transactions-queue-2";
    public static final String ERROR_QUEUE_NAME = "error-queue";
    public static final String EXCHANGE_NAME = "transactions-exchange";
    public static final String ROUTING_KEY_NAME = "transactions.routing.key";
    public static final String ROUTING_KEY_ERROR = "error.routing.key";
    @Value("${rabbit.uri}")
    public static String URI_NAME;

    @Bean
    public static URI uri(@Value("${rabbit.uri}") String uri) {
        URI_NAME = uri;
        return URI.create(uri);
    }
    @Bean
    public AmqpAdmin amqpAdmin(URI uri) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(uri);
        var amqpAdmin =  new RabbitAdmin(connectionFactory);

        var exchange = new TopicExchange(EXCHANGE_NAME);
        var queue = new Queue(QUEUE_NAME, true, false, false);
        var queue2 = new Queue(QUEUE_NAME_2, true, false, false);
        var errorQueue = new Queue(ERROR_QUEUE_NAME, true, false, false);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareQueue(queue2);
        amqpAdmin.declareQueue(errorQueue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_NAME));
        amqpAdmin.declareBinding(BindingBuilder.bind(queue2).to(exchange).with(ROUTING_KEY_NAME));
        amqpAdmin.declareBinding(BindingBuilder.bind(errorQueue).to(exchange).with(ROUTING_KEY_ERROR));

        return amqpAdmin;
    }


    @Bean
    public ConnectionFactory connectionFactory(URI uri) throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.useNio();
        connectionFactory.setUri(uri);
        return connectionFactory;
    }

    @Bean
    public Mono<Connection> connectionMono(@Value("spring.application.name") String name, ConnectionFactory connectionFactory)  {
        return Mono.fromCallable(() -> connectionFactory.newConnection(name)).cache();
    }

    @Bean
    public SenderOptions senderOptions(Mono<Connection> connectionMono) {
        return new SenderOptions()
                .connectionMono(connectionMono)
                .resourceManagementScheduler(Schedulers.boundedElastic());
    }

    @Bean
    public Sender sender(SenderOptions senderOptions) {
        return RabbitFlux.createSender(senderOptions);
    }


    @Bean
    public ReceiverOptions receiverOptions(Mono<Connection> connectionMono) {
        return new ReceiverOptions()
                .connectionMono(connectionMono);
    }

    @Bean
    public Receiver receiver(ReceiverOptions receiverOptions) {
        return RabbitFlux.createReceiver(receiverOptions);
    }
}
