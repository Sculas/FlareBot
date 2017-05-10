package stream.flarebot.flarebot.commands.secret;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.objects.GuildWrapper;

public class QuitCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (sender.getId().equals("158310004187725824") || sender.getId().equals("155954930191040513")) {
            FlareBot.getInstance().quit(false);
        }
    }

    @Override
    public String getCommand() {
        return "quit";
    }

    @Override
    public String getDescription() {
        return "Dev only command";
    }

    @Override
    public String getUsage() {
        return "{%}quit";
    }

    @Override
    public CommandType getType() {
        return CommandType.HIDDEN;
    }

    @Override
    public boolean isDefaultPermission() {
        return false;
    }
}
