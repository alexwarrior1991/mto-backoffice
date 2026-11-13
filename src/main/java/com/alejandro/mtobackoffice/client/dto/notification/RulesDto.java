package com.alejandro.mtobackoffice.client.dto.notification;

import java.util.List;
import java.util.Map;

/**
 * {@code GET /admin/rules}: las reglas cargadas, de donde salieron y las variables con las que se
 * evaluan.
 */
public record RulesDto(String source, Map<String, Object> variables, List<RuleDto> rules) {

    public RulesDto {
        variables = variables == null ? Map.of() : variables;
        rules = rules == null ? List.of() : List.copyOf(rules);
    }
}
