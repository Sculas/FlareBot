package stream.flarebot.flarebot.commands.music;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;

public class ShuffleCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        FlareBot.instance().getMusicManager().getPlayer(channel.getGuild().getId()).shuffle();
    }

    @Override
    public String getCommand() {
        return "shuffle";
    }

    @Override
    public String getDescription() {
        return "Shuffle up the order of the songs";
    }

    @Override
    public String getUsage() {
        return "`{%}shuffle` - Shuffles order of the songs.";
    }

    @Override
    public Permission getPermission() {
        return Permission.SHUFFLE_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }

    @Override
    public boolean isDefaultPermission() {
        return true;
    }
}
