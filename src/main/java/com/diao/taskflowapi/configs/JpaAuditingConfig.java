package com.diao.taskflowapi.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Active le remplissage automatique des champs {@code createdAt} / {@code updatedAt}
 * definis dans {@link com.diao.taskflowapi.entities.BaseEntity}.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}