package EPA.Cuenta_Bancaria_Web.handlers.bus;


import EPA.Cuenta_Bancaria_Web.models.DTO.M_Cuenta_DTO;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.Receiver;

@Component
public class RabbitMqMessageConsumer implements CommandLineRunner {

    @Autowired
    private Receiver receiver;

    @Autowired
    private Gson gson;


    @Override
    public void run(String... args) throws Exception {
        receiver.consumeAutoAck(RabbitConfig.QUEUE_NAME)
                .map(message -> {
                   M_Cuenta_DTO transaction = gson
                           .fromJson(new String(message.getBody()),
                                   M_Cuenta_DTO.class);

                    System.out.println("La cuenta creada fue:  " + transaction);
                    return transaction;
                }).subscribe();
    }
}
