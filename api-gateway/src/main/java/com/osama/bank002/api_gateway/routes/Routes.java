package com.osama.bank002.api_gateway.routes;

import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class Routes {

	// ===== API traffic =====
//	PROFILE-SERVICE
	@Bean
	public RouterFunction<ServerResponse> profileRoute() {
		return RouterFunctions.route()
				.route(RequestPredicates.path("/api/profiles/**"), HandlerFunctions.http("http://localhost:8080"))
				.build();
	}

//	ACCOUNT-SERVICE
	@Bean
	public RouterFunction<ServerResponse> accountRoute() {
		return RouterFunctions.route()
				.route(RequestPredicates.path("/api/accounts/**"), HandlerFunctions.http("http://localhost:8081"))
				.build();
	}

//	transfers-SERVICE
	@Bean
	public RouterFunction<ServerResponse> transferRoute() {
		return RouterFunctions.route()
				.route(RequestPredicates.path("/api/transfers/**"), HandlerFunctions.http("http://localhost:8082"))
				.build();
	}

// transactions-Service
	@Bean
	public RouterFunction<ServerResponse> transactionRoute() {
		return RouterFunctions.route()
				.route(RequestPredicates.path("/api/transactions/**"), HandlerFunctions.http("http://localhost:8085"))
				.route(RequestPredicates.path("/api/statements/**"), HandlerFunctions.http("http://localhost:8085"))
				.build();
	}

//	beneficiary-SERVICE
@Bean
public RouterFunction<ServerResponse> beneficiaryRoute() {
	var base = RequestPredicates.path("/api/beneficiaries");
	var subs = RequestPredicates.path("/api/beneficiaries/**");

	return RouterFunctions.route()
			.route(base.or(subs), HandlerFunctions.http("http://localhost:8084"))
			.build();
}

	@Bean
	public RouterFunction<ServerResponse> cardRoute() {
		return RouterFunctions.route()
				.route(RequestPredicates.path("/api/cards/**"), HandlerFunctions.http("http://localhost:8083")).build();
	}

//	AUTH-SERVICE
	@Bean
	public RouterFunction<ServerResponse> authRoute() {
		return RouterFunctions.route()
				.route(RequestPredicates.path("/auth/**"), HandlerFunctions.http("http://localhost:8099")).build();
	}

	// ===== Swagger aggregation (hit /v3/api-docs directly) =====
//	PROFILE-SERVICE
	@Bean
	public RouterFunction<ServerResponse> profileSwagger() {
		return RouterFunctions.route().route(RequestPredicates.path("/aggregate/profile-service/v3/api-docs"),
				HandlerFunctions.http("http://localhost:8080/v3/api-docs")).build();
	}

//	ACCOUNT-SERVICE
	@Bean
	public RouterFunction<ServerResponse> accountSwagger() {
		return RouterFunctions.route().route(RequestPredicates.path("/aggregate/account-service/v3/api-docs"),
				HandlerFunctions.http("http://localhost:8081/v3/api-docs")).build();
	}

//	transfers-SERVICE

	@Bean
	public RouterFunction<ServerResponse> transferSwagger() {
		return RouterFunctions.route().route(RequestPredicates.path("/aggregate/transfer-service/v3/api-docs"),
				HandlerFunctions.http("http://localhost:8082/v3/api-docs")).build();
	}

	// transactions-Service
	@Bean
	public RouterFunction<ServerResponse> transactionSwagger() {
		return RouterFunctions.route().route(RequestPredicates.path("/aggregate/transaction-service/v3/api-docs"),
				HandlerFunctions.http("http://localhost:8085/v3/api-docs")).build();
	}

//	beneficiary-SERVICE
	@Bean
	public RouterFunction<ServerResponse> beneficiarySwagger() {
		return RouterFunctions.route().route(RequestPredicates.path("/aggregate/beneficiary-service/v3/api-docs"),
				HandlerFunctions.http("http://localhost:8084/v3/api-docs")).build();
	}

	@Bean
	public RouterFunction<ServerResponse> cardSwagger() {
		return RouterFunctions.route().route(RequestPredicates.path("/aggregate/card-service/v3/api-docs"),
				HandlerFunctions.http("http://localhost:8083/v3/api-docs")).build();
	}

	// Fallback (optional)
	@Bean
	public RouterFunction<ServerResponse> fallbackRoute() {
		return RouterFunctions.route().GET("/fallbackRoute", req -> ServerResponse
				.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service Unavailable, please try again later")).build();
	}
}