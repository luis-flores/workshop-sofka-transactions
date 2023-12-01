package EPA.Cuenta_Bancaria_Web.handlers.bus;

import EPA.Cuenta_Bancaria_Web.RabbitConfig;
import EPA.Cuenta_Bancaria_Web.drivenAdapters.repositorios.I_RepositorioCuentaMongo;
import EPA.Cuenta_Bancaria_Web.drivenAdapters.repositorios.I_Repositorio_TransaccionMongo;
import EPA.Cuenta_Bancaria_Web.models.DTO.M_Cliente_DTO;
import EPA.Cuenta_Bancaria_Web.models.DTO.M_Cuenta_DTO;
import EPA.Cuenta_Bancaria_Web.models.DTO.M_Transaccion_DTO;
import EPA.Cuenta_Bancaria_Web.models.Mongo.M_ClienteMongo;
import EPA.Cuenta_Bancaria_Web.models.Mongo.M_CuentaMongo;
import EPA.Cuenta_Bancaria_Web.services.Cuenta.Cuenta_ImpMongo;
import EPA.Cuenta_Bancaria_Web.services.Transaccion.Transaccion_ImpMongo;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.Receiver;

@Component
public class RabbitMqErrorHandler implements CommandLineRunner {

    @Autowired
    private Receiver receiver;

    @Autowired
    private Gson gson;

    @Autowired
    Cuenta_ImpMongo servicioCuenta;

    @Autowired
    Transaccion_ImpMongo servicioTransaccion;

    @Override
    public void run(String... args) throws Exception {
        receiver.consumeAutoAck(RabbitConfig.ERROR_QUEUE_NAME)
            .map(message -> {
                M_Transaccion_DTO transaccion = gson
                    .fromJson(new String(message.getBody()),
                        M_Transaccion_DTO.class);

                M_Cuenta_DTO cuenta = transaccion.getCuenta();
                cuenta.setSaldo_Global(transaccion.getSaldo_inicial());
                servicioCuenta.actualizarSaldo(
                    new M_Cuenta_DTO(
                        cuenta.getId(),
                        new M_Cliente_DTO(
                            cuenta.getCliente().getId(),
                            cuenta.getCliente().getNombre()
                        ),
                        cuenta.getSaldo_Global()
                    )
                );

                servicioTransaccion.borrar(
                    new M_Transaccion_DTO(
                        transaccion.getId(),
                        transaccion.getCuenta(),
                        transaccion.getMonto_transaccion(),
                        transaccion.getSaldo_inicial(),
                        transaccion.getSaldo_final(),
                        transaccion.getCosto_tansaccion(),
                        transaccion.getTipo()
                    )
                );

                System.out.println("La transaccion a reversar fue:  " + transaccion);
                return transaccion;
            }).subscribe();
    }
}
