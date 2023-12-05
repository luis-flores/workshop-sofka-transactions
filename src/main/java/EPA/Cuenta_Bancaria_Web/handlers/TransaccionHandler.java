package EPA.Cuenta_Bancaria_Web.handlers;

import EPA.Cuenta_Bancaria_Web.models.DTO.M_Transaccion_DTO;
import EPA.Cuenta_Bancaria_Web.models.Enum_Tipos_Deposito;
import EPA.Cuenta_Bancaria_Web.services.Transaccion.I_Transaccion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class TransaccionHandler {
    @Autowired
    private I_Transaccion transaccionService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        Flux<M_Transaccion_DTO> transacciones = transaccionService.findAll();

        return ServerResponse.ok()
            .body(transacciones, M_Transaccion_DTO.class);
    }

    public Mono<ServerResponse> procesarDepositoCajero(ServerRequest request) {
        String idCuenta = request.pathVariable("id_Cuenta");
        BigDecimal monto = new BigDecimal(request.pathVariable("monto"));

        Mono<M_Transaccion_DTO> transaccion = transaccionService.Procesar_Deposito(idCuenta, Enum_Tipos_Deposito.CAJERO, monto);

        return ServerResponse.ok()
            .body(transaccion, M_Transaccion_DTO.class);
    }

    public Mono<ServerResponse> procesarDepositoSucursal(ServerRequest request) {
        String idCuenta = request.pathVariable("id_Cuenta");
        BigDecimal monto = new BigDecimal(request.pathVariable("monto"));

        Mono<M_Transaccion_DTO> transaccion = transaccionService.Procesar_Deposito(idCuenta, Enum_Tipos_Deposito.SUCURSAL, monto);

        return ServerResponse.ok()
            .body(transaccion, M_Transaccion_DTO.class);
    }

    public Mono<ServerResponse> procesarDepositoCuenta(ServerRequest request) {
        String idCuenta = request.pathVariable("id_Cuenta");
        BigDecimal monto = new BigDecimal(request.pathVariable("monto"));

        Mono<M_Transaccion_DTO> transaccion = transaccionService.Procesar_Deposito(idCuenta, Enum_Tipos_Deposito.OTRA_CUENTA, monto);

        return ServerResponse.ok()
            .body(transaccion, M_Transaccion_DTO.class);
    }
}
