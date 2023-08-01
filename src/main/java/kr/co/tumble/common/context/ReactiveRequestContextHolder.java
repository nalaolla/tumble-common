package kr.co.tumble.common.context;

import org.springframework.http.HttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * ReactiveRequestContextHolder
 */
public class ReactiveRequestContextHolder {

    private ReactiveRequestContextHolder () {}

    public static final Class<ServerHttpRequest> CONTEXT_KEY = ServerHttpRequest.class;

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(Mono::just).map(ctx -> ctx.get(CONTEXT_KEY));
    }

    public static Mono<URI> getURI() {
        return getRequest().map(HttpRequest::getURI);
    }

}