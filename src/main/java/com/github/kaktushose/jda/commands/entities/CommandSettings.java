package com.github.kaktushose.jda.commands.entities;

import com.github.kaktushose.jda.commands.exceptions.CommandException;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides a very basic level of configuration.
 *
 * <p>All settings declared through this class can also be declared inside a yaml file. To be loaded properly the yaml file
 * must be located in the resources folder and its name must be <em>settings.yaml</em>. If the yaml file deviates from
 * that you can use the {@link com.github.kaktushose.jda.commands.internal.YamlLoader} to load the file manually.
 *
 * @author Kaktushose
 * @version 1.0.0
 * @see com.github.kaktushose.jda.commands.internal.YamlLoader
 * @since 1.0.0
 */
public class CommandSettings {

    private final static Logger log = LoggerFactory.getLogger(CommandSettings.class);
    private final Set<Long> mutedChannels;
    private final Set<Long> mutedUsers;
    private final Map<Long, String> guildPrefixes;
    private final Set<String> helpLabels;
    private final Map<String, Set<Long>> permissionHolders;
    private boolean botMentionPrefix;
    private String prefix;
    private boolean ignoreBots, ignoreLabelCase;

    /**
     * Constructs a new CommandSettings object with default values.
     */
    public CommandSettings() {
        this.prefix = "!";
        ignoreBots = true;
        ignoreLabelCase = true;
        botMentionPrefix = true;
        mutedChannels = new HashSet<>();
        mutedUsers = new HashSet<>();
        guildPrefixes = new HashMap<>();
        permissionHolders = new HashMap<>();
        helpLabels = new HashSet<>();
        helpLabels.add("help");
    }

    /**
     * Constructs a new CommandSettings object.
     *
     * @param prefix           the prefix the framework will listen to
     * @param ignoreBots       whether the framework should ignore messages from Discord Bots or not
     * @param ignoreLabelCase  whether the command mapper should be case sensitive or not
     * @param botMentionPrefix whether to allow a bot mention to be a valid prefix or not
     */
    public CommandSettings(@Nullable String prefix, boolean ignoreBots, boolean ignoreLabelCase, boolean botMentionPrefix) {
        this.prefix = validatePrefix(prefix);
        this.ignoreBots = ignoreBots;
        this.ignoreLabelCase = ignoreLabelCase;
        this.botMentionPrefix = botMentionPrefix;
        mutedChannels = new HashSet<>();
        mutedUsers = new HashSet<>();
        guildPrefixes = new HashMap<>();
        permissionHolders = new HashMap<>();
        helpLabels = new HashSet<>();
        helpLabels.add("help");
    }

    /**
     * Get the default prefix.
     *
     * @return the default prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the default prefix. If the prefix is {@code null} the fallback prefix <em>!</em> will be used instead.
     *
     * @param prefix the default prefix to set
     * @return the current instance to use fluent interface
     */
    public CommandSettings setPrefix(@Nullable String prefix) {
        this.prefix = validatePrefix(prefix);
        return this;
    }

    /**
     * Set a custom prefix for a specific guild. This will override the default prefix.
     * If the prefix is {@code null} the fallback prefix <em>!</em> will be used instead.
     *
     * @param guildId the id of the guild to set the prefix for
     * @param prefix  the prefix to set
     * @return the current instance to use fluent interface
     */
    public CommandSettings addGuildPrefix(long guildId, @Nullable String prefix) {
        guildPrefixes.put(guildId, validatePrefix(prefix));
        return this;
    }

    /**
     * Set custom prefixes for the given guilds. This will override the default prefix.
     * If one of the provided prefixes is {@code null} the fallback prefix <em>!</em> will be used instead.
     *
     * @param guildPrefixes a map that contains the prefixes to set for each guild. Key: the guild id Value: the prefix
     * @return the current instance to use fluent interface
     */
    public CommandSettings addGuildPrefixes(@Nonnull Map<Long, String> guildPrefixes) {
        guildPrefixes.forEach(this::addGuildPrefix);
        return this;
    }

    /**
     * Removes a custom prefix for the specified guild. This will automatically reactivate the default prefix.
     *
     * @param guildId the id of the guild to remove the custom prefix from
     * @return the current instance to use fluent interface
     */
    public CommandSettings removeGuildPrefix(long guildId) {
        guildPrefixes.remove(guildId);
        return this;
    }

    /**
     * Removes all specified custom prefixes. This will automatically reactivate the default prefix.
     *
     * @return the current instance to use fluent interface
     */
    public CommandSettings clearGuildPrefixes() {
        guildPrefixes.clear();
        return this;
    }

    /**
     * Get the prefix for the specified guild.
     *
     * @param guildId the id of the guild to get the prefix for
     * @return if present the custom guild prefix, else the default prefix
     */
    public String getGuildPrefix(long guildId) {
        String prefix = guildPrefixes.get(guildId);
        return prefix == null ? this.prefix : prefix;
    }

    /**
     * Get the prefix for the specified guild.
     *
     * @param guild the {@code Guild} to get the prefix for
     * @return if present the custom guild prefix, else the default prefix
     */
    public String getGuildPrefix(@Nonnull Guild guild) {
        return getGuildPrefix(guild.getIdLong());
    }

    /**
     * Get a Set of the ids of all users with the given permission. This Set is mutable and thus can be modified in
     * order to add or remove users from the given permission level. Returns an empty Set if no users have the
     * given permission level.
     *
     * @param permission the permission to get the users for
     * @return a mutable Set containing the ids of all users with the given permission
     * @see com.github.kaktushose.jda.commands.annotations.Permission
     */
    public Set<Long> getPermissionHolders(@Nonnull String permission) {
        permissionHolders.putIfAbsent(permission, new HashSet<>());
        return permissionHolders.get(permission);
    }

    /**
     * Get a Map of all permissions and their holders. Key: the String representing a permission. Value: a
     * Set of the ids of all users with the permission. This Map is mutable and thus can be modified in
     * order to add or remove permissions and their holders.
     *
     * @return a mutable Map containing all permissions and the ids of all users with the permissions
     */
    public Map<String, Set<Long>> getAllPermissionHolders() {
        return permissionHolders;
    }

    /**
     * Get a Set of the ids of all muted TextChannels. This Set is mutable and thus can be modified in order to mute or unmute a
     * TextChannel.
     *
     * @return a mutable Set containing the ids of all muted TextChannels.
     */
    public Set<Long> getMutedChannels() {
        return mutedChannels;
    }

    /**
     * Get a Set of the ids of all muted Users. This Set is mutable and thus can be modified in order to mute or unmute a
     * User.
     *
     * @return a mutable Set containing the ids of all muted Users.
     */
    public Set<Long> getMutedUsers() {
        return mutedUsers;
    }

    /**
     * Get a Set of all specified help labels. This Set is mutable and thus can be modified in order to add or remove a
     * help label. The default value is <em>help</em>.
     *
     * @return a mutable Set containing all help labels.
     */
    public Set<String> getHelpLabels() {
        return helpLabels;
    }

    /**
     * Whether bots should be ignored by this framework or not. If set true, bot accounts can also trigger commands.
     *
     * @return {@code true} if bot accounts
     */
    public boolean isIgnoreBots() {
        return ignoreBots;
    }

    /**
     * Set if bots should be ignored by this framework or not. If set false, bot accounts can also trigger commands.
     *
     * @param ignoreBots true if bots should not invoke commands
     * @return the current instance to use fluent interface
     */
    public CommandSettings setIgnoreBots(boolean ignoreBots) {
        this.ignoreBots = ignoreBots;
        return this;
    }

    /**
     * Whether the case of a label should be ignored or not. For instance, if set true, input <em>FOO</em> will match
     * the label <em>foo</em>
     *
     * @return {@code true} if the label case is ignored
     */
    public boolean isIgnoreLabelCase() {
        return ignoreLabelCase;
    }

    /**
     * Set the case of a label should be ignored or true. For instance, if set true, input <em>FOO</em> will match
     * the label <em>foo</em>
     *
     * @param ignoreLabelCase true if the label case should be ignored
     * @return the current instance to use fluent interface
     */
    public CommandSettings setIgnoreLabelCase(boolean ignoreLabelCase) {
        this.ignoreLabelCase = ignoreLabelCase;
        return this;
    }

    /**
     * Whether a mention of the bot account should be a valid prefix or not.
     *
     * @return {@code true} if a bot mention is a valid prefix
     */
    public boolean isBotMentionPrefix() {
        return botMentionPrefix;
    }

    /**
     * Set if a mention of the bot account should be a valid prefix or not.
     *
     * @param botMentionPrefix {@code true} if a bot mention is a valid prefix
     * @return the current instance to use fluent interface
     */
    public CommandSettings setBotMentionPrefix(boolean botMentionPrefix) {
        this.botMentionPrefix = botMentionPrefix;
        return this;
    }

    private String validatePrefix(String prefix) {
        if (prefix == null) {
            log.error("Switching to default value '!'", new CommandException("Prefix must not be null!"));
            return "!";
        }
        return prefix;
    }

}
