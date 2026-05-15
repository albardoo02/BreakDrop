package net.azisaba.breakdrop.config;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.variables.VariableRegistry;
import io.lumine.mythic.core.skills.variables.VariableScope;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

public final class ConfigVariableCondition implements Condition {
    private final VariableScope scope;
    private final String key;
    private final VariableType type;
    private final String value;
    private final EqOp op;

    public ConfigVariableCondition(@NotNull VariableScope scope, @NotNull String key, @NotNull VariableType type, @NotNull String value, @NotNull EqOp op) {
        if (scope != VariableScope.CASTER && scope != VariableScope.GLOBAL) {
            throw new IllegalArgumentException("Invalid scope: " + scope);
        }
        if (type == VariableType.STRING && op != EqOp.EQ) {
            throw new IllegalArgumentException("Invalid op: " + op);
        }
        type.checkType(value);
        this.scope = scope;
        this.key = key;
        this.type = type;
        this.value = value;
        this.op = op;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ConfigVariableCondition load(@NotNull ConfigurationSection section) {
        String rawScope = Objects.requireNonNull(section.getString("scope"), "scope");
        VariableScope scope = VariableScope.valueOf(rawScope.toUpperCase(Locale.ROOT));
        String key = Objects.requireNonNull(section.getString("key"), "key");
        String rawType = Objects.requireNonNull(section.getString("type"), "type");
        VariableType type = VariableType.valueOf(rawType.toUpperCase(Locale.ROOT));
        String value = Objects.requireNonNull(section.getString("value"), "value");
        String rawOp = Objects.requireNonNull(section.getString("op"), "op");
        EqOp op = EqOp.of(rawOp.toUpperCase(Locale.ROOT));
        return new ConfigVariableCondition(scope, key, type, value, op);
    }

    @Contract(pure = true)
    public @NotNull VariableScope getScope() {
        return scope;
    }

    @Contract(pure = true)
    public @NotNull String getKey() {
        return key;
    }

    @Contract(pure = true)
    public @NotNull VariableType getType() {
        return type;
    }

    @Contract(pure = true)
    public @NotNull String getValue() {
        return value;
    }

    @Contract(pure = true)
    public @NotNull EqOp getOp() {
        return op;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public boolean check(@NotNull Player player, @NotNull Block block) {
        VariableRegistry registry;
        if (getScope() == VariableScope.CASTER) {
            registry = MythicBukkit.inst().getPlayerManager().getProfile(player).getVariables();
        } else {
            registry = MythicBukkit.inst().getVariableManager().getGlobalRegistry();
        }
        switch (getType()) {
            case STRING:
                return Objects.equals(registry.getString(getKey()), getValue());
            case INT: {
                int value = registry.getInt(getKey());
                int target = Integer.parseInt(getValue());
                switch (getOp()) {
                    case EQ:
                        return value == target;
                    case NE:
                        return value != target;
                    case GT:
                        return value > target;
                    case GE:
                        return value >= target;
                    case LT:
                        return value < target;
                    case LE:
                        return value <= target;
                    default:
                        throw new AssertionError();
                }
            }
            case FLOAT: {
                float value = registry.getFloat(getKey());
                float target = Float.parseFloat(getValue());
                switch (getOp()) {
                    case EQ:
                        return value == target;
                    case NE:
                        return value != target;
                    case GT:
                        return value > target;
                    case GE:
                        return value >= target;
                    case LT:
                        return value < target;
                    case LE:
                        return value <= target;
                    default:
                        throw new AssertionError();
                }
            }
        }
        return false;
    }
}
