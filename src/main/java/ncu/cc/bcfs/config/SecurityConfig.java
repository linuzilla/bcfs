package ncu.cc.bcfs.config;

import ncu.cc.bcfs.constants.Roles;
import ncu.cc.bcfs.constants.Routes;
import ncu.cc.bcfs.properties.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(ClientProperties.class)
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    MapReactiveUserDetailsService userDetailsService(ClientProperties clientProperties) {
        PasswordEncoder encoder = passwordEncoder();
        List<UserDetails> list = new ArrayList<>();

        clientProperties.getClients().forEach((k, v) -> {
            String pass = encoder.encode(new String(Base64.getDecoder().decode(v.getPassword())));
            logger.info("adduser {} / {}", v.getUser(), pass);
            list.add(User.withUsername(v.getUser()).password(pass).roles(Roles.USER).build());
        });

        return new MapReactiveUserDetailsService(list);
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange()
                .pathMatchers(Routes.STORE + "/**").hasRole(Roles.USER)
                .pathMatchers(Routes.RETRIEVE + "/**").permitAll()
                .anyExchange().hasRole(Roles.ADMIN)
                .and().httpBasic()
                .and().csrf().disable()
                .build();
    }
}
