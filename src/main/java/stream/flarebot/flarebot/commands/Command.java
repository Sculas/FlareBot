package stream.flarebot.flarebot.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import stream.flarebot.flarebot.FlareBotManager;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.PerGuildPermissions;

import java.util.EnumSet;

public interface Command {

    void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member);

    String getCommand();

    String getDescription();

    String getUsage();

    CommandType getType();

    default String getExtraInfo() {
        return null;
    }

    stream.flarebot.flarebot.permissions.Permission getPermission();

    default EnumSet<Permission> getDiscordPermission() {
        return getPermission().getDiscordPerm();
    }

    default String[] getAliases() {
        return new String[]{};
    }

    default PerGuildPermissions getPermissions(TextChannel chan) {
        return FlareBotManager.instance().getGuild(chan.getGuild().getId()).getPermissions();
    }

    default boolean isDefaultPermission() {
        return getPermission() != null && getPermission().isDefaultPerm() && !getType().isInternal();
    }

    default boolean deleteMessage() {
        return true;
    }

    default boolean isBetaTesterCommand() {
        return false;
    }

    default char getPrefix(Guild guild) {
        return FlareBotManager.instance().getGuild(guild.getId()).getPrefix();
    }
}