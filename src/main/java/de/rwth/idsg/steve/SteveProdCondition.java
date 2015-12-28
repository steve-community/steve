package de.rwth.idsg.steve;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.core.type.AnnotatedTypeMetadata;

import static de.rwth.idsg.steve.SteveConfiguration.CONFIG;

/**
 * We might also have used {@link Profile} for registering beans depending on profile,
 * but it only accepts String as value (which is not type safe) and we use enums
 * in {@link ApplicationProfile}. The newer {@link Condition} and {@link Conditional} APIs
 * are more flexible by being programmatic anyway.
 *
 * Typing the String value of the enum is not an option, because it might cause problems
 * in future when we change or refactor something. This is how technical debt starts
 * and then everything falls apart.
 *
 *
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 28.12.2015
 */
public class SteveProdCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return CONFIG.getProfile().isProd();
    }
}
