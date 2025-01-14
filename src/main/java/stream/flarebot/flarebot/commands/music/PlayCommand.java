package stream.flarebot.flarebot.commands.music;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Region;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.music.VideoThread;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.Constants;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.general.GuildUtils;

import java.awt.Color;
import java.time.LocalDateTime;

public class PlayCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length > 0) {
            if (channel.getGuild().getRegion() == Region.EU_WEST || channel.getGuild().getRegion() == Region.VIP_EU_WEST) {
                if (LocalDateTime.now().getHour() == 0 && LocalDateTime.now().getMinute() == 0 && LocalDateTime.now().getSecond() == 0) {
                    channel.sendMessage(new EmbedBuilder().setTitle("Jesus Quist", null).setDescription("It's quite late to be listening to music! You should be asleep! " +
                            ":zzz: :night_with_stars:").setColor(Color.blue).build()).queue();
                    Constants.logEG("Jesus Quist - Late night", this, guild.getGuild(), sender);
                }
            }
            if (member.getVoiceState().inVoiceChannel()) {
                if (channel.getGuild().getAudioManager().isAttemptingToConnect()) {
                    MessageUtils.sendErrorMessage("Currently connecting to a voice channel! Try again soon!", channel);
                    return;
                }
                if (channel.getGuild().getSelfMember().getVoiceState().inVoiceChannel() &&
                        !(channel.getGuild().getSelfMember().getVoiceState().getAudioChannel().getId()
                                .equals(member.getVoiceState().getAudioChannel().getId()))) {
                    MessageUtils.sendErrorMessage("I cannot join your channel! I am already in a channel!", channel);
                    return;
                }
                GuildUtils.joinChannel(channel, member);
            }
            if (args[0].startsWith("http") || args[0].startsWith("www.")) {
                VideoThread.getThread(args[0], channel, sender).start();
            } else {
                String term = MessageUtils.getMessage(args, 0);
                VideoThread.getSearchThread(term, channel, sender).start();
            }
        } else
            MessageUtils.sendUsage(this, channel, sender, args);
    }

    @Override
    public String getCommand() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Searches for songs on YouTube";
    }

    @Override
    public String getUsage() {
        return "`{%}play <search_term/URL>` - Searches for a song on YouTube.";
    }

    @Override
    public Permission getPermission() {
        return Permission.PLAY_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSIC;
    }
}
