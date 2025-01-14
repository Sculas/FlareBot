package stream.flarebot.flarebot.commands.random;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.general.GuildUtils;

import java.awt.Color;

public class AvatarCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        User user = sender;
        if (args.length > 0)
            user = GuildUtils.getUser(MessageUtils.getMessage(args, 0), guild.getGuildId());
        if (user != null) {
            if (!user.getId().equals(sender.getId()) && !getPermissions(channel).hasPermission(member, Permission.AVATAR_OTHER)) {
                MessageUtils.sendErrorMessage("You need the permission `" + Permission.AVATAR_OTHER + "` in order to do that command!",
                        channel);
                return;
            }
            channel.sendMessage(MessageUtils.getEmbed(sender).setColor(Color.cyan).setAuthor(user.getName(), null, null)
                    .setImage(user.getEffectiveAvatarUrl()).build()).queue();
        } else
            MessageUtils.sendErrorMessage("Cannot find that user!", channel);
    }

    @Override
    public String getCommand() {
        return "avatar";
    }

    @Override
    public String getDescription() {
        return "Grab a users avatar";
    }

    @Override
    public String getUsage() {
        return "`{%}avatar [user]` - Grab your or another users avatar";
    }

    @Override
    public Permission getPermission() {
        return Permission.AVATAR_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.RANDOM;
    }
}
