package stream.flarebot.flarebot.commands.moderation.mod;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.joda.time.Period;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.mod.modlog.ModAction;
import stream.flarebot.flarebot.mod.modlog.ModlogHandler;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.general.GeneralUtils;
import stream.flarebot.flarebot.util.general.GuildUtils;

public class TempBanCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length >= 2) {
            User user = GuildUtils.getUser(args[0], guild.getGuildId());
            if (user == null) {
                MessageUtils.sendErrorMessage("We cannot find that user! Try their ID if you didn't already.", channel, sender);
                return;
            }
            String reason = null;
            if (args.length >= 3)
                reason = MessageUtils.getMessage(args, 2);

            Period period = GeneralUtils.getTimeFromInput(args[1], channel);
            if (period == null) return;

            ModlogHandler.getInstance().handleAction(guild, channel, sender, user, ModAction.TEMP_BAN, reason,
                    period.toStandardDuration().getMillis());
        } else {
            MessageUtils.sendUsage(this, channel, sender, args);
        }
    }

    @Override
    public String getCommand() {
        return "tempban";
    }

    @Override
    public String getDescription() {
        return "Temp bans a user";
    }

    @Override
    public String getUsage() {
        return "`{%}tempban <user> <time> [reason]` - Temp bans a user.";
    }

    @Override
    public Permission getPermission() {
        return Permission.TEMPBAN_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }
}
