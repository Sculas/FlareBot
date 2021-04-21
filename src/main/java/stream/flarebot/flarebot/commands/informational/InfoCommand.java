package stream.flarebot.flarebot.commands.informational;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.Getters;
import stream.flarebot.flarebot.GitHandler;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.Constants;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.general.GeneralUtils;
import stream.flarebot.flarebot.util.implementations.MultiSelectionContent;

import java.awt.Color;
import java.util.function.Supplier;

public class InfoCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length == 0) {
            EmbedBuilder bld = MessageUtils.getEmbed()
                    .setThumbnail(MessageUtils.getAvatar(channel.getJDA().getSelfUser()))
                    .setFooter("Maintained by Lucas#3456", channel.getJDA().getSelfUser().getEffectiveAvatarUrl())
                    .setDescription("FlareBot v" + FlareBot.getVersion() + " Information")
                    .setColor(Color.CYAN);
            for (Content content : Content.values) {
                bld.addField(content.getName(), content.getReturn(), content.isAlign());
            }
            channel.sendMessage(bld.build()).queue();
        } else
            GeneralUtils.handleMultiSelectionCommand(sender, channel, args, Content.values);
    }

    @Override
    public String getCommand() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Displays info about the bot.";
    }

    @Override
    public String getUsage() {
        return "`{%}info [section]` - Sends info about the bot.";
    }

    @Override
    public Permission getPermission() {
        return Permission.INFO_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }

    public enum Content implements MultiSelectionContent<String, String, Boolean> {

        SERVERS("Servers", () -> String.valueOf(Getters.getGuildCache().size())),
        VERSION("Version", FlareBot.getVersion()),
        JDA_VERSION("JDA version", JDAInfo.VERSION),
        GIT("Git Revision", (GitHandler.getLatestCommitId() != null ? GitHandler.getLatestCommitId() : "Unknown")),
        SOURCE("Source", "[`GitHub`](https://github.com/Sculas/FlareBot)"),
        INVITE("Invite", String.format("[`Invite`](%s)", FlareBot.getInvite())),
        EMPTY("\u200B", "\u200B", false),
        SUPPORT_SERVER("Support Server", Constants.INVITE_MARKDOWN),
        MADE_BY("Made by: ", "Walshy, Arsen, BinaryOverload"),
        MAINTAINED_BY("Maintained by:", "Lucas#3456");

        private final String name;
        private final Supplier<String> returns;
        private boolean align = true;

        public static Content[] values = values();

        Content(String name, String returns) {
            this.name = name;
            this.returns = () -> returns;
        }

        Content(String name, String returns, boolean align) {
            this.name = name;
            this.returns = () -> returns;
            this.align = align;
        }

        Content(String name, Supplier<String> returns) {
            this.name = name;
            this.returns = returns;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getReturn() {
            return returns.get();
        }

        @Override
        public Boolean isAlign() {
            return this.align;
        }
    }
}
