package stream.flarebot.flarebot.commands.moderation.mod;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.mod.modlog.ModAction;
import stream.flarebot.flarebot.mod.modlog.ModlogHandler;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.general.GuildUtils;

public class UnbanCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length >= 1) {
            User target = GuildUtils.getUser(MessageUtils.getMessage(args, 0), null, true);
            if (target == null) {
                MessageUtils.sendErrorMessage("We cannot find that user! Try their ID if you didn't already.\n"
                                + "To get a User ID simply enable developer mode by going to User Settings > Appearance " +
                                "> Developer Mode then just right click the user and click Copy ID",
                        channel, sender);
                return;
            }
            ModlogHandler.getInstance().handleAction(guild, channel, sender, target, ModAction.UNBAN, null);
        } else {
            MessageUtils.sendUsage(this, channel, sender, args);
        }
    }

    @Override
    public String getCommand() {
        return "unban";
    }

    @Override
    public String getDescription() {
        return "Unban a user from the server";
    }

    @Override
    public String getUsage() {
        return "`{%}unban <user>` - Unbans a user";
    }

    @Override
    public Permission getPermission() {
        return Permission.UNBAN_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }
}
