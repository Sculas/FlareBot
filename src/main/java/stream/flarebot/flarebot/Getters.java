package stream.flarebot.flarebot;

import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.utils.cache.SnowflakeCacheView;
import stream.flarebot.flarebot.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Getters {

    private static FlareBot flareBot() {
        return FlareBot.instance();
    }

    // getXCache
    public static SnowflakeCacheView<Guild> getGuildCache() {
        return getShardManager().getGuildCache();
    }

    public static SnowflakeCacheView<TextChannel> getTextChannelCache() {
        return getShardManager().getTextChannelCache();
    }

    public static SnowflakeCacheView<VoiceChannel> getVoiceChannelCache() {
        return getShardManager().getVoiceChannelCache();
    }

    public static SnowflakeCacheView<User> getUserCache() {
        return getShardManager().getUserCache();
    }

    // getXById
    @Nullable
    public static TextChannel getChannelById(String id) {
        return getShardManager().getTextChannelById(id);
    }

    @Nullable
    public static TextChannel getChannelById(long id) {
        return getShardManager().getTextChannelById(id);
    }

    @Nullable
    public static Guild getGuildById(String id) {
        return getShardManager().getGuildById(id);
    }

    @Nullable
    public static Guild getGuildById(long id) {
        return getShardManager().getGuildById(id);
    }

    @Nullable
    public static Emote getEmoteById(long emoteId) {
        return getShardManager().getEmoteById(emoteId);
    }

    @Nullable
    public static User getUserById(String id) {
        return getShardManager().getUserById(id);
    }

    @Nullable
    public static User getUserById(long id) {
        return getShardManager().getUserById(id);
    }

    @Nullable
    public static Role getRoleById(String id) {
        return getShardManager().getRoleById(id);
    }

    @Nullable
    public static Role getRoleById(long id) {
        return getShardManager().getRoleById(id);
    }


    @Nullable
    public static User retrieveUserById(long id) {
        try {
            return flareBot().getClient().retrieveUserById(id).complete();
        } catch (ErrorResponseException e) {
            return null;
        }
    }

    // Audio
    public static long getConnectedVoiceChannels() {
        return getShardManager().getGuildCache().stream()
                .filter(c -> c.getAudioManager().getConnectedChannel() != null).count();
    }

    public static Set<VoiceChannel> getConnectedVoiceChannel() {
        return getShardManager().getGuildCache().stream().filter(c -> c.getAudioManager().getConnectedChannel() != null)
                .map(g -> g.getAudioManager().getConnectedChannel())
                .collect(Collectors.toSet());
    }

    public static long getActiveVoiceChannels() {
        return getShardManager().getGuildCache().stream()
                .filter(c -> c.getAudioManager().getConnectedChannel() != null)
                .map(g -> flareBot().getMusicManager().getPlayer(g.getId()))
                .filter(p -> p != null && p.getPlayingTrack() != null && !p.getPaused())
                .count();
    }

    public static int getSongsQueued() {
        return flareBot().getMusicManager().getPlayers().stream()
                .mapToInt(p -> p.getPlaylist().size())
                .sum();
    }

    // Other
    public static List<JDA> getShards() {
        return flareBot().getShardManager().getShards();
    }

    @Nonnull
    public static SelfUser getSelfUser() {
        return flareBot().getClient().getSelfUser();
    }

    @Nonnull
    public static ShardManager getShardManager() {
        return flareBot().getShardManager();
    }

    public static Guild getOfficialGuild() {
        return getGuildById(Constants.OFFICIAL_GUILD);
    }
}
