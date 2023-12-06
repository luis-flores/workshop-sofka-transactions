package EPA.Cuenta_Bancaria_Web.routes;

import EPA.Cuenta_Bancaria_Web.handlers.CuentaHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@AllArgsConstructor
public class CuentaRouter {
    private CuentaHandler cuenta;

    @Bean
    public RouterFunction<ServerResponse> cuentaRoutes() {
        return RouterFunctions.route()
            .GET("/Cuentas/listar_cuentas", cuenta::findAll)
            .POST("/Cuentas/Crear", cuenta::crear)

            .build();
    }
}
