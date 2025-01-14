package stream.flarebot.flarebot.commands.secret.internal;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.commands.InternalCommand;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.PerGuildPermissions;
import stream.flarebot.flarebot.util.GitHubUtils;

public class ChangelogCommand implements InternalCommand {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message msg, String[] args, Member member) {
        if (PerGuildPermissions.isAdmin(sender)) {
            if (args.length == 0) {
                channel.sendMessage("Specify a version or PR to post about!").queue();
                return;
            }
            if (args[0].startsWith("pr:")) {
                channel.sendMessage(new MessageBuilder().setEmbed(GitHubUtils.getEmbedForPR(args[0].substring(3))
                        .build()).build()).queue();
            } else {
                String message = msg.getContentRaw();
                message = message.substring(message.indexOf(" ") + 1);
                channel.sendMessage(message).queue();
            }
        }
    }

    @Override
    public String getCommand() {
        return "changelog";
    }

    @Override
    public String getDescription() {
        return "Get version changelogs";
    }

    @Override
    public String getUsage() {
        return "{%}changelog <version>\n`{%}changelog pr:<prNum>`";
    }

    @Override
    public CommandType getType() {
        return CommandType.INTERNAL;
    }
}
