package stream.flarebot.flarebot.commands.informational;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import stream.flarebot.flarebot.FlareBot;
import stream.flarebot.flarebot.Getters;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.Constants;
import stream.flarebot.flarebot.util.ShardUtils;

import java.util.Arrays;

public class StatusCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        int quaterShards = Getters.getShards().size() / 4;
        double ping = Getters.getShards().stream().mapToLong(JDA::getPing).average().orElse(-1);

        int deadShard = 0;
        int reconnecting = 0;
        int connecting = 0;
        int noVoiceConnections = 0;
        int highResponseTime = 0;

        for (int shardId = 0; shardId < Getters.getShards().size(); shardId++) {
            JDA jda = ShardUtils.getShardById(shardId);
            if (jda == null) {
                connecting++;
                continue;
            }

            boolean reconnect = ShardUtils.isReconnecting(shardId);
            if (ShardUtils.isDead(shardId))
                deadShard++;
            if (reconnect)
                reconnecting++;
            if (jda.getVoiceChannelCache().stream().noneMatch(vc -> vc.getMembers().contains(vc.getGuild().getSelfMember())))
                noVoiceConnections++;
            if (ShardUtils.getLastEventTime(shardId) >= 1500 && !reconnect)
                highResponseTime++;
        }
        StringBuilder sb = new StringBuilder();
        if (reconnecting > Math.min(quaterShards, 10))
            sb.append("⚠ WARNING: A lot of shards are currently reconnecting! This could mean the bot is unable to be " +
                    "used on several thousand servers for a few minutes! (").append(reconnecting).append(" shards reconnecting)").append("\n");
        if (highResponseTime > Math.min(quaterShards, 20))
            sb.append("⚠ WARNING: We seem to be experiencing a high event time on quite a few shards, this is usually " +
                    "down to discord not wanting to co-op with us :( please be patient while these ")
                    .append(highResponseTime).append(" shards go back to normal!").append("\n");
        if (deadShard > 5)
            sb.append(" SEVERE: We have quite a few dead shards! Please report this on the [Support Server](")
                    .append(Constants.INVITE_URL).append(")").append("\n");

        String status = deadShard == 0 && highResponseTime == 0 && reconnecting < (Math.max(quaterShards, 5))
                ? "Good! :)" : "Issues :/";
        if (deadShard > 5)
            status = "Severe issues! Discord could be dying! @EVERYONE RUN!";
        sb.append("Bot Status: ").append(status).append("\n\n");

        sb.append(String.format("FlareBot Version: %s\n" +
                        "JDA Version: %s\n" +
                        "Current Shard: %s\n" +
                        "* Average Ping: %s\n" +
                        "* Ping By Shard: %s\n" +
                        "* Dead Shards: %s shards\n" +
                        "* No Voice Connections: %s shards\n" +
                        "* Shards Reconnecting: %s shards\n" +
                        "* Shards Connecting: %s shards\n" +
                        "* High Last Event Time: %s shards\n" +
                        "Guilds: %d | Users: %d | Connected VCs: %d | Active VCs: %d",
                FlareBot.getVersion(),
                JDAInfo.VERSION,
                channel.getJDA().getShardInfo() == null ? 0 : channel.getJDA().getShardInfo().getShardId(),
                ping,
                Arrays.toString(ShardUtils.getPingsForShards()),
                deadShard,
                noVoiceConnections,
                reconnecting,
                connecting,
                highResponseTime,
                Getters.getGuildCache().size(),
                Getters.getUserCache().size(),
                Getters.getConnectedVoiceChannels(),
                Getters.getActiveVoiceChannels()
        ));

        channel.sendMessage("**FlareBot's Status**\n```prolog\n" + sb.toString() + "\n```").queue();
    }

    @Override
    public String getCommand() {
        return "status";
    }

    @Override
    public String getDescription() {
        return "Get status info about the bot.";
    }

    @Override
    public String getUsage() {
        return "`{%}status [shardId]` - Check the status of the bot [or a shard]";
    }

    @Override
    public Permission getPermission() {
        return Permission.STATUS_COMMAND;
    }

    @Override
    public CommandType getType() {
        return CommandType.GENERAL;
    }
}
