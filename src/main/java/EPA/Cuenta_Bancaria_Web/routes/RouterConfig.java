package EPA.Cuenta_Bancaria_Web.routes;

import EPA.Cuenta_Bancaria_Web.handlers.CuentaHandler;
import EPA.Cuenta_Bancaria_Web.handlers.TransaccionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Autowired
    private CuentaHandler cuenta;

    @Autowired
    private TransaccionHandler transaccion;

    @Bean
    public RouterFunction<ServerResponse> cuentaListar() {
        return RouterFunctions.route()
            .GET("/Cuentas/listar_cuentas", cuenta::findAll)
            .POST("/Cuentas/Crear", cuenta::crear)

            .GET("/Transacciones/listar_transacciones", transaccion::findAll)
            .POST("/Transacciones/Crear/Deposito/Cajero/{id_Cuenta}/{monto}", transaccion::procesarDepositoCajero)
            .POST("/Transacciones/Crear/Deposito/Sucursal/{id_Cuenta}/{monto}", transaccion::procesarDepositoSucursal)
            .POST("/Transacciones/Crear/Deposito/OtraCuenta/{id_Cuenta}/{monto}", transaccion::procesarDepositoCuenta)

            .build();
    }
}
