package ncu.cc.bcfs.security;

import ncu.cc.commons.utils.StackTraceUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
public class MyAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, AccessDeniedException e) {
        StackTraceUtil.print1(serverWebExchange.getRequest().getURI().toString());
        StackTraceUtil.print1(e.getMessage());
        return Mono.empty();
    }
}
