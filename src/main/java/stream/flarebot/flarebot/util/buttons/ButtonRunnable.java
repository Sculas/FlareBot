package stream.flarebot.flarebot.util.buttons;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

public interface ButtonRunnable {

    void run(long ownerID, User user, Message message);
}
