package ncu.cc.bcfs.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SecurityServiceImpl implements SecurityService {
    @Override
    public Mono<String> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.just(securityContext.getAuthentication().getName()));
    }
}
