package EPA.Cuenta_Bancaria_Web.services.Cuenta;

import EPA.Cuenta_Bancaria_Web.drivenAdapters.bus.RabbitMqPublisher;
import EPA.Cuenta_Bancaria_Web.models.DTO.M_Cliente_DTO;
import EPA.Cuenta_Bancaria_Web.models.DTO.M_Cuenta_DTO;
import EPA.Cuenta_Bancaria_Web.models.Mongo.M_ClienteMongo;
import EPA.Cuenta_Bancaria_Web.models.Mongo.M_CuentaMongo;
import EPA.Cuenta_Bancaria_Web.RabbitConfig;
import EPA.Cuenta_Bancaria_Web.drivenAdapters.repositorios.I_RepositorioCuentaMongo;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.math.BigDecimal;


@Service()
@Qualifier("MONGO")
public class Cuenta_ImpMongo implements I_Cuenta
{
    @Autowired
    I_RepositorioCuentaMongo repositorio_Cuenta;

    @Autowired
    private RabbitMqPublisher eventBus;

    @Autowired
    private Sender sender;

    @Override
    public Mono<M_Cuenta_DTO> crear_Cuenta(M_Cuenta_DTO p_Cuenta_DTO)
    {
        M_CuentaMongo cuenta = new M_CuentaMongo(p_Cuenta_DTO.getId(),
                new M_ClienteMongo(p_Cuenta_DTO.getCliente().getId(),
                        p_Cuenta_DTO.getCliente().getNombre()),
                p_Cuenta_DTO.getSaldo_Global());


        eventBus.publishMessage(cuenta);

        return repositorio_Cuenta.save(cuenta)
                .map(cuentaModel-> {
                    return new M_Cuenta_DTO(cuentaModel.getId(),
                            new M_Cliente_DTO(cuentaModel.getCliente().getId(),
                                    cuentaModel.getCliente().getNombre()),
                            cuentaModel.getSaldo_Global());
                });
    }

    @Override
    public Flux<M_Cuenta_DTO> findAll()
    {
        return repositorio_Cuenta.findAll()
                .map(cuentaModel -> new M_Cuenta_DTO(cuentaModel.getId(),
                        new M_Cliente_DTO(cuentaModel.getCliente().getId(),
                                cuentaModel.getCliente().getNombre()),
                        cuentaModel.getSaldo_Global()));
    }

    @Override
    public Mono<M_Cuenta_DTO> actualizarSaldo(M_Cuenta_DTO p_Cuenta) {
        String id = p_Cuenta.getId();
        M_CuentaMongo cuenta = repositorio_Cuenta.findById(id).block();

        cuenta.setSaldo_Global(p_Cuenta.getSaldo_Global());

        repositorio_Cuenta.save(cuenta).block();

//        repositorio_Cuenta.findById(id)
//            .map(cuentaEncontrada -> {
//                System.out.println("Cuenta encontrada id: " + cuentaEncontrada.getId());
//
//                System.out.println("Saldo actualizado de: " + cuentaEncontrada.getSaldo_Global());
//                cuentaEncontrada.setSaldo_Global(p_Cuenta.getSaldo_Global());
//                System.out.println("Saldo actualizado a: " + cuentaEncontrada.getSaldo_Global());
//
//                return cuentaEncontrada;
//            })
//            .subscribe();

        return Mono.just(new M_Cuenta_DTO(
            cuenta.getId(),
            new M_Cliente_DTO(
                cuenta.getCliente().getId(),
                cuenta.getCliente().getNombre()),
            cuenta.getSaldo_Global()
        ));
        /*
        return repositorio_Cuenta.findById(p_Cuenta.getId())
            .map(cuenta -> {
                // aplicar cambio de saldo al objeto cuenta con los datos actuales en db
                cuenta.setSaldo_Global(p_Cuenta.getSaldo_Global());

                repositorio_Cuenta.save(cuenta);
                return new M_Cuenta_DTO(
                    cuenta.getId(),
                    new M_Cliente_DTO(
                        cuenta.getCliente().getId(),
                        cuenta.getCliente().getNombre()),
                    cuenta.getSaldo_Global()
                );
            });*/
    }
}