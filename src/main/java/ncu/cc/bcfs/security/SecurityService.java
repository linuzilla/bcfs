package ncu.cc.bcfs.security;

import reactor.core.publisher.Mono;

public interface SecurityService {
    Mono<String> currentUser();
}
