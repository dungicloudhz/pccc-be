package com.cozyquoteforge.pccc.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleConstraintUpdater implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
            jdbcTemplate.execute("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ROLE_USER','ROLE_EDITOR','ROLE_ADMIN'))");
            log.info("Updated users_role_check constraint to include ROLE_EDITOR");
        } catch (Exception ex) {
            log.warn("Could not update users_role_check constraint: {}", ex.getMessage());
        }
    }
}
