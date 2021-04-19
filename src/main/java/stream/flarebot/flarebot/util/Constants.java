package stream.flarebot.flarebot.util;

import java.time.Clock;
import java.time.LocalDateTime;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.Getters;
import stream.flarebot.flarebot.commands.Command;

public class Constants {

    public static final long OFFICIAL_GUILD = 833768100646092861L;
    private static final String FLAREBOT_API = "https://api.flarebot.stream";
    private static final String FLAREBOT_API_DEV = "http://localhost:8880";
    public static final String INVITE_URL = "https://discord.gg/bRaBACvpMC";
    public static final String INVITE_MARKDOWN = "[Support Server](" + INVITE_URL + ")";

    public static final long DEVELOPER_ID = 833768157118070787L;
    public static final long ADMINS_ID = 833770155829493760L;
    public static final long CONTRIBUTOR_ID = 833770190986674176L;

    private static final String FLARE_TEST_BOT_CHANNEL = "833770591315689542";
    public static final char COMMAND_CHAR = '_';
    public static final String COMMAND_CHAR_STRING = String.valueOf(COMMAND_CHAR);

    @Deprecated
    public static Guild getOfficialGuild() {
        return Getters.getGuildById(OFFICIAL_GUILD);
    }

    public static TextChannel getErrorLogChannel() {
        return (FlareBot.instance().isTestBot() ?
                Getters.getChannelById(Constants.FLARE_TEST_BOT_CHANNEL) :
                Getters.getChannelById("833772347847999488"));
    }

    public static TextChannel getGuildLogChannel() {
        return (FlareBot.instance().isTestBot() ?
                Getters.getChannelById(Constants.FLARE_TEST_BOT_CHANNEL) :
                Getters.getChannelById("833772264180154409"));
    }

    private static TextChannel getEGLogChannel() {
        return (FlareBot.instance().isTestBot() ?
                Getters.getChannelById(Constants.FLARE_TEST_BOT_CHANNEL) :
                Getters.getChannelById("833772299051728896"));
    }

    public static void logEG(String eg, Command command, Guild guild, User user) {
        EmbedBuilder builder = new EmbedBuilder().setTitle("Found `" + eg + "`")
                .addField("Guild", guild.getId() + " (`" + guild.getName() + "`) ", true)
                .addField("User", user.getAsMention() + " (`" + user.getName() + "#" + user.getDiscriminator() + "`)", true)
                .setTimestamp(LocalDateTime.now(Clock.systemUTC()));
        if (command != null) builder.addField("Command", command.getCommand(), true);
        Constants.getEGLogChannel().sendMessage(builder.build()).queue();
    }

    public static TextChannel getImportantLogChannel() {
        return (FlareBot.instance().isTestBot() ?
                Getters.getChannelById(Constants.FLARE_TEST_BOT_CHANNEL) :
                Getters.getChannelById("833772422167396382"));
    }

    public static String getAPI() {
        return FlareBot.instance().isTestBot() ? FLAREBOT_API_DEV : FLAREBOT_API;
    }
}
