package net.azisaba.breakdrop.config;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import net.azisaba.breakdrop.util.ConfigUtil;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ConfigDropFunction {
    private final List<String> skills;
    private final boolean skillsRandom;
    private final double chance;
    private final int count;
    private final Condition condition;

    @Contract(pure = true)
    public ConfigDropFunction(
            @NotNull List<String> skills,
            boolean skillsRandom,
            double chance,
            int count,
            @NotNull Condition condition
    ) {
        this.skills = skills;
        this.skillsRandom = skillsRandom;
        this.chance = chance;
        this.count = count;
        this.condition = condition;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ConfigDropFunction load(@NotNull ConfigurationSection section) {
        List<String> skills = section.getStringList("skills").stream().filter(Objects::nonNull).collect(Collectors.toList());
        boolean skillsRandom = section.getBoolean("skills_random", false);
        double chance = section.getDouble("chance", 1.0);
        int count = section.getInt("count", 1);
        Condition condition;
        if (section.contains("condition")) {
            ConfigurationSection subSection = Objects.requireNonNull(ConfigUtil.toConfigurationSection(section.get("condition")));
            if (subSection.contains("op")) {
                condition = ConfigConditionList.load(subSection);
            } else {
                condition = ConfigCondition.load(subSection);
            }
        } else {
            condition = Condition.alwaysTrue();
        }
        return new ConfigDropFunction(skills, skillsRandom, chance, count, condition);
    }

    @Contract(pure = true)
    public @NotNull List<@NotNull String> getSkills() {
        return skills;
    }

    @Contract(pure = true)
    public boolean isSkillsRandom() {
        return skillsRandom;
    }

    @Contract(pure = true)
    public double getChance() {
        return chance;
    }

    @Contract(pure = true)
    public int getCount() {
        return count;
    }

    @Contract(pure = true)
    public @NotNull Condition getCondition() {
        return condition;
    }

    public boolean rollChance() {
        return Math.random() < getChance();
    }

    public boolean canExecute(@NotNull Player player, @NotNull Block block) {
        return getCondition().check(player, block);
    }

    public void execute(@NotNull Player player, @NotNull Block block) {
        if (isSkillsRandom()) {
            String skill = getSkills().get((int) (Math.random() * getSkills().size()));
            executeSkill(player, skill);
        } else {
            for (String skill : getSkills()) {
                executeSkill(player, skill);
            }
        }
    }

    private static final BukkitAPIHelper API_HELPER = new BukkitAPIHelper();

    private static void executeSkill(@NotNull Player player, @NotNull String skillName) {
        //API_HELPER.castSkill(player, skillName, null, player.getLocation(), Collections.singleton(player), null, 1.0f);
        //API_HELPER.castSkill(player, skillName, null, player.getLocation(), null, Collections.singleton(player.getLocation()), 1.0f);
        //API_HELPER.castSkill(player, skillName, null, player.getLocation(), Collections.singleton(player), Collections.singleton(player.getLocation()), 1.0f);
        AbstractPlayer mythicPlayer = BukkitAdapter.adapt(player);
        AbstractLocation loc = BukkitAdapter.adapt(player.getLocation());
        SkillMetadata meta =
                new SkillMetadataImpl(
                        SkillTriggers.BLOCK_BREAK,
                        new GenericCaster(mythicPlayer),
                        mythicPlayer,
                        loc,
                        new HashSet<>(Collections.singleton(mythicPlayer)),
                        new HashSet<>(Collections.singleton(loc)),
                        0f);
        meta.getVariables().putString("equip-slot", "HAND");
        MythicBukkit.inst().getSkillManager().getSkill(skillName).ifPresent(skill -> skill.execute(meta));
    }
}
