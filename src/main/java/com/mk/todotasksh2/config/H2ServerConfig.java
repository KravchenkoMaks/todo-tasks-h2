package com.mk.todotasksh2.config;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

/**
 * Init H2 server to add opportunity connect from Idea
 * <p>
 * <p> jdbc:h2:tcp://localhost:9092/mem:todoDB
 * <p> login: sa
 * <p> without password
 */
@Slf4j
@Configuration
@Profile("!test")
public class H2ServerConfig {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        log.info("Start H2 TCP server");
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

}
