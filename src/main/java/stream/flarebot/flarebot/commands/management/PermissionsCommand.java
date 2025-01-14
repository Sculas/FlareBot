package stream.flarebot.flarebot.commands.management;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import stream.flarebot.flarebot.commands.Command;
import stream.flarebot.flarebot.commands.CommandType;
import stream.flarebot.flarebot.objects.GuildWrapper;
import stream.flarebot.flarebot.objects.GuildWrapperLoader;
import stream.flarebot.flarebot.permissions.Group;
import stream.flarebot.flarebot.permissions.PerGuildPermissions;
import stream.flarebot.flarebot.permissions.Permission;
import stream.flarebot.flarebot.util.MessageUtils;
import stream.flarebot.flarebot.util.buttons.ButtonGroupConstants;
import stream.flarebot.flarebot.util.general.GeneralUtils;
import stream.flarebot.flarebot.util.general.GuildUtils;
import stream.flarebot.flarebot.util.pagination.PagedEmbedBuilder;
import stream.flarebot.flarebot.util.pagination.PaginationUtil;

import java.awt.Color;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PermissionsCommand implements Command {

    @Override
    public void onCommand(User sender, GuildWrapper guild, TextChannel channel, Message message, String[] args, Member member) {
        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("group")) {
                String groupString = args[1];
                Group group = getPermissions(channel).getGroup(groupString);
                if (args.length >= 3) {
                    if (group == null && !args[2].equalsIgnoreCase("create")) {
                        MessageUtils.sendErrorMessage("That group doesn't exist! You can create it with `{%}permissions group " + groupString + " create`", channel);
                        return;
                    } else if (args[2].equalsIgnoreCase("add")) {
                        if (args.length == 4) {
                            if (!Permission.isValidPermission(args[3])) {
                                MessageUtils.sendErrorMessage("That is an invalid permission! Permissions start with `flarebot.` followed with a command name!\n" +
                                        "**Example:** `flarebot.play`\n" +
                                        "See `_permissions list` for a full list!", channel);
                                return;
                            }
                            if (group.addPermission(args[3])) {
                                MessageUtils.sendSuccessMessage("Successfully added the permission `" + args[3] + "` to the group `" + groupString + "`", channel, sender);
                                return;
                            } else {
                                MessageUtils.sendErrorMessage("Couldn't add the permission (it probably already exists)", channel);
                                return;
                            }

                        }
                    } else if (args[2].equalsIgnoreCase("remove")) {
                        if (args.length == 4) {
                            if (group.removePermission(args[3])) {
                                MessageUtils.sendSuccessMessage("Successfully removed the permission `" + args[3] + "` from the group `" + groupString + "`", channel, sender);
                                return;
                            } else {
                                MessageUtils.sendErrorMessage("Couldn't remove the permission (it probably didn't exist)", channel);
                                return;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("create")) {
                        if (!GuildWrapperLoader.ALLOWED_CHARS_REGEX.matcher(groupString).matches()) {
                            if (groupString.length() > 32)
                                MessageUtils.sendErrorMessage("Please make sure the group name is a maximum of 32 chars!", channel);
                            else if (groupString.length() < 3)
                                MessageUtils.sendErrorMessage("Please make sure the group name is a minimum of 3 chars!", channel);
                            else
                                MessageUtils.sendErrorMessage("This group name has invalid characters! Please only use alphanumeric characters (Letters and numbers) and any of these: `" + new String(GuildWrapperLoader.ALLOWED_SPECIAL_CHARACTERS) + "`", channel);
                            return;
                        }
                        if (getPermissions(channel).addGroup(groupString)) {
                            MessageUtils.sendSuccessMessage("Successfully created group: `" + groupString + "`", channel, sender);
                            return;
                        } else {
                            MessageUtils.sendErrorMessage("That group already exists!!", channel);
                            return;
                        }
                    } else if (args[2].equalsIgnoreCase("delete")) {
                        getPermissions(channel).deleteGroup(groupString);
                        MessageUtils.sendSuccessMessage("Deleted group `" + groupString + "`", channel, sender);
                        return;
                    } else if (args[2].equalsIgnoreCase("link")) {
                        if (args.length == 4) {
                            Role role = GuildUtils.getRole(args[3], guild.getGuildId());
                            if (role != null) {
                                group.linkRole(role.getId());
                                MessageUtils.sendSuccessMessage("Successfully linked the group `" + groupString + "` to the role `" + role.getName() + "`", channel, sender);
                                return;
                            } else {
                                MessageUtils.sendErrorMessage("That role doesn't exist!", channel);
                                return;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("unlink")) {
                        Role role;
                        if (group.getRoleId() == null || (role =
                                guild.getGuild().getRoleById(group.getRoleId())) == null) {
                            MessageUtils.sendErrorMessage("Cannot unlink if a role isn't linked!!", channel);
                            return;
                        } else {
                            group.linkRole(null);
                            MessageUtils.sendSuccessMessage("Successfully unlinked the role " + role.getName() + " from the group " + group.getName(), channel, sender);
                            return;
                        }
                    } else if (args[2].equalsIgnoreCase("list")) {
                        if (args.length <= 4) {
                            int page = args.length == 4 ? Integer.valueOf(args[3]) : 1;
                            Set<String> perms = group.getPermissions();
                            List<String> permList = GeneralUtils.orderList(perms);

                            String list = permList.stream().collect(Collectors.joining("\n"));

                            PagedEmbedBuilder<String> pe =
                                    new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list, PaginationUtil.SplitMethod.NEW_LINES, 25));
                            pe.setTitle("Permissions for the group: " + group.getName());
                            pe.enableCodeBlock();

                            PaginationUtil.sendEmbedPagedMessage(pe.build(), page - 1, channel, sender, ButtonGroupConstants.PERMISSIONS_GROUP);
                            return;
                        }
                    } else if (args[2].equalsIgnoreCase("massadd")) {
                        if (args.length == 4) {
                            List<Member> roleMembers;
                            String roleName = "";
                            switch (args[3]) {
                                case "@everyone":
                                    roleMembers = guild.getGuild().getMembers();
                                    roleName = "everyone";
                                    break;
                                case "@here":
                                    roleMembers = channel.getMembers();
                                    roleName = "here";
                                    break;
                                default:
                                    Role role = GuildUtils.getRole(args[3], guild.getGuildId());
                                    if (role != null) {
                                        roleMembers = guild.getGuild().getMembersWithRoles(role);
                                    } else {
                                        MessageUtils.sendErrorMessage("That role doesn't exist!!", channel);
                                        return;
                                    }
                                    break;
                            }
                            for (Member user : roleMembers) {
                                getPermissions(channel).getUser(user).addGroup(group);
                            }
                            MessageUtils.sendSuccessMessage("Successfully added the group `" + groupString + "` to everyone in the role @" + roleName, channel, sender);
                            return;

                        }
                    } else if (args[2].equalsIgnoreCase("clear")) {
                        group.getPermissions().clear();
                        MessageUtils.sendSuccessMessage("Cleared all permissions from the group: " + group.getName(), channel);
                        return;
                    } else if (args[2].equalsIgnoreCase("move") && args.length >= 4) {
                        int pos = GeneralUtils.getInt(args[3], -1);
                        if (pos < 1 || pos >= guild.getPermissions().getGroups().size()) {
                            MessageUtils.sendWarningMessage("Invalid Position: " + args[3], channel);
                            return;
                        } else {
                            guild.getPermissions().moveGroup(group, pos - 1);
                            MessageUtils.sendSuccessMessage("Moved group `" + groupString + "` to position " + pos, channel, sender);
                            return;
                        }
                    } else if (args[2].equalsIgnoreCase("clone") && args.length >= 4) {
                        if (guild.getPermissions().cloneGroup(group, args[3])) {
                            MessageUtils.sendMessage("Cloned group Successfully", channel);
                            return;
                        } else {
                            MessageUtils.sendWarningMessage("Error cloning group (The group might already exist)", channel);
                            return;
                        }
                    } else if (args[2].equalsIgnoreCase("rename") && args.length >= 4) {
                        if (guild.getPermissions().renameGroup(group, args[3])) {
                            MessageUtils.sendMessage("Renamed group Successfully", channel);
                            return;
                        } else {
                            MessageUtils.sendWarningMessage("Error renaming group (The destination group might already exist)", channel);
                            return;
                        }
                    }
                }
            } else if (args[0].equalsIgnoreCase("user")) {
                String userString = args[1];
                User user = GuildUtils.getUser(userString, guild.getGuildId());
                if (user == null) {
                    MessageUtils.sendErrorMessage("That user doesn't exist!!", channel);
                    return;
                }
                stream.flarebot.flarebot.permissions.User permUser =
                        getPermissions(channel).getUser(guild.getGuild().getMember(user));
                if (args.length >= 3) {
                    if (args[2].equalsIgnoreCase("group")) {
                        if (args.length >= 4) {
                            if (args[3].equalsIgnoreCase("add")) {
                                if (args.length == 5) {
                                    String groupString = args[4];
                                    Group group = getPermissions(channel).getGroup(groupString);
                                    if (group == null) {
                                        MessageUtils.sendErrorMessage("That group doesn't exists!! You can create it with `"
                                                + getPrefix(channel.getGuild()) + "permissions group " + groupString
                                                + " create`", channel);
                                        return;
                                    }
                                    permUser.addGroup(group);
                                    MessageUtils.sendSuccessMessage("Successfully added the group `" + groupString
                                            + "` to " + user.getAsMention(), channel, sender);
                                    return;
                                }
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                if (args.length == 5) {
                                    String groupString = args[4];
                                    Group group = getPermissions(channel).getGroup(groupString);
                                    if (group == null) {
                                        MessageUtils.sendErrorMessage("That group doesn't exists!!", channel);
                                        return;
                                    }
                                    if (permUser.removeGroup(group)) {
                                        MessageUtils.sendSuccessMessage("Successfully removed the group `" + groupString
                                                + "` from " + user.getAsMention(), channel, sender);
                                        return;
                                    } else {
                                        MessageUtils.sendErrorMessage("The user doesn't have that group!!", channel);
                                        return;
                                    }
                                }
                            } else if (args[3].equalsIgnoreCase("list")) {
                                int page = (args.length == 5 ? GeneralUtils.getInt(args[4], 1) : 1) - 1;
                                Set<String> groups = new HashSet<>(permUser.getGroups());
                                groups.addAll(getPermissions(channel)
                                        .getGroups()
                                        .stream()
                                        .filter(g -> guild.getGuild().getMember(user).getRoles()
                                                .contains(guild.getGuild().getRoleById(g.getRoleId()))
                                                || g.getRoleId().equals(guild.getGuildId()))
                                        .map(Group::getName)
                                        .collect(Collectors.toList()));
                                List<String> groupList = GeneralUtils.orderList(groups);

                                String list = groupList.stream().collect(Collectors.joining("\n"));

                                PagedEmbedBuilder pe = new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list,
                                        PaginationUtil.SplitMethod.NEW_LINES, 25))
                                        .setTitle("Groups for " + MessageUtils.getTag(user)).enableCodeBlock();

                                PaginationUtil.sendEmbedPagedMessage(pe.build(), page, channel, sender, ButtonGroupConstants.PERMISSIONS_USER_GROUPS);
                                return;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("permission")) {
                        if (args.length >= 4) {
                            if (args[3].equalsIgnoreCase("add")) {
                                if (args.length == 5) {
                                    if (!Permission.isValidPermission(args[4])) {
                                        MessageUtils.sendErrorMessage("That is an invalid permission! Permissions start with `flarebot.` followed with a command name!\n" +
                                                "**Example:** `flarebot.play`\n" +
                                                "See `_permissions list` for a full list!", channel);
                                        return;
                                    }
                                    if (permUser.addPermission(args[4])) {
                                        MessageUtils.sendSuccessMessage("Successfully added the permission `" + args[4] + "` to " + user.getAsMention(), channel, sender);
                                        return;
                                    } else {
                                        MessageUtils.sendErrorMessage("The user doesn't have that permission!!", channel);
                                        return;
                                    }
                                }
                            } else if (args[3].equalsIgnoreCase("remove")) {
                                if (args.length == 5) {
                                    if (permUser.removePermission(args[4])) {
                                        MessageUtils.sendSuccessMessage("Successfully removed the permission `" + args[4] + "` from " + user.getAsMention(), channel, sender);
                                        return;
                                    } else {
                                        MessageUtils.sendErrorMessage("The user already has that permission!!", channel);
                                        return;
                                    }
                                }
                            } else if (args[3].equalsIgnoreCase("list")) {
                                int page = (args.length == 5 ? Integer.valueOf(args[4]) : 1) - 1;
                                Set<String> perms = permUser.getPermissions();
                                List<String> permList = GeneralUtils.orderList(perms);

                                String list = permList.stream().collect(Collectors.joining("\n"));

                                PagedEmbedBuilder<String> pe =
                                        new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(list, PaginationUtil.SplitMethod.NEW_LINES, 25));
                                pe.setTitle("Permissions for " + MessageUtils.getTag(user));
                                pe.enableCodeBlock();
                                PaginationUtil.sendEmbedPagedMessage(pe.build(), page, channel, sender, ButtonGroupConstants.PERMISSIONS_USER_PERMISSIONS);
                                return;
                            }
                        }
                    } else if (args[2].equalsIgnoreCase("check")) {
                        if (getPermissions(channel).hasPermission(guild.getGuild().getMember(user), Permission.ALL_PERMISSIONS)) {
                            EmbedBuilder builder = new EmbedBuilder();
                            builder.setTitle("Permissions for " + user.getName());
                            builder.setDescription("**All Permissions!**");
                            channel.sendMessage(builder.build()).queue();
                            return;
                        } else {
                            String perms = Arrays.stream(Permission.values())
                                    .filter(p -> getPermissions(channel).hasPermission(guild.getGuild().getMember(user), p))
                                    .map(m -> "`" + m + "`")
                                    .collect(Collectors.joining("\n"));
                            PagedEmbedBuilder<String> embedBuilder =
                                    new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(perms, PaginationUtil.SplitMethod.NEW_LINES, 20));
                            embedBuilder.setTitle("Permissions for " + MessageUtils.getTag(user));
                            PaginationUtil.sendEmbedPagedMessage(embedBuilder.build(), 0, channel, sender, ButtonGroupConstants.PERMISSIONS_USER_CHECK);
                            return;
                        }
                    } else if (args[2].equalsIgnoreCase("clear")) {
                        permUser.getPermissions().clear();
                        MessageUtils.sendSuccessMessage("Cleared all permissions from: " + MessageUtils.getTag(user), channel);
                        return;
                    }
                }
            }
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("groups")) {
                if (this.getPermissions(channel).getGroups().isEmpty()) {
                    channel.sendMessage(MessageUtils.getEmbed(sender)
                            .setColor(Color.RED)
                            .setDescription("There are no groups for this guild!")
                            .build()).queue();
                    return;
                } else {
                    int page = args.length == 2 ? Integer.valueOf(args[1]) : 1;

                    StringBuilder stringBuilder = new StringBuilder();
                    int i = 1;
                    for (Group group : guild.getPermissions().getGroups()) {
                        stringBuilder.append(i).append(". ").append(group.getName()).append("\n");
                        i++;
                    }

                    PagedEmbedBuilder<String> pe =
                            new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(stringBuilder.toString(), PaginationUtil.SplitMethod.NEW_LINES, 20));
                    pe.setTitle("Groups");
                    pe.enableCodeBlock();
                    PaginationUtil.sendEmbedPagedMessage(pe.build(), page - 1, channel, sender, ButtonGroupConstants.PERMISSIONS_GROUPS);
                    return;
                }
            } else if (args[0].equalsIgnoreCase("list")) {
                StringBuilder defaultPerms = new StringBuilder("**Default Permissions**\n");
                StringBuilder nonDefaultPerms = new StringBuilder("**Non-Default Permissions**\n");
                for (Permission p : Permission.values()) {
                    if (p == Permission.ALL_PERMISSIONS) continue;
                    if (p.isDefaultPerm())
                        defaultPerms.append("`").append(p).append("`").append("\n");
                    else
                        nonDefaultPerms.append("`").append(p).append("`").append("\n");
                }
                PagedEmbedBuilder<String> embedBuilder =
                        new PagedEmbedBuilder<>(PaginationUtil.splitStringToList(
                                defaultPerms.append("\n").append(nonDefaultPerms.toString()).toString(),
                                PaginationUtil.SplitMethod.NEW_LINES,
                                20
                        ));
                embedBuilder.setTitle("Permissions");
                PaginationUtil.sendEmbedPagedMessage(embedBuilder.build(), 0, channel, sender, ButtonGroupConstants.PERMISSIONS_LIST);
                return;
            } else if (args[0].equalsIgnoreCase("reset")) {
                guild.setPermissions(new PerGuildPermissions());
                MessageUtils.sendSuccessMessage("Successfully reset perms", channel, sender);
                return;
            }
        }
        MessageUtils.sendUsage(this, channel, sender, args);
    }

    @Override
    public String getCommand() {
        return "permissions";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"perm", "perms"};
    }

    @Override
    public String getDescription() {
        return "Manages server-wide permissions for FlareBot.";
    }


    //TODO: Pagination
    @Override
    public String getUsage() {
        return "`{%}permissions group <group> add|remove <perm>` - Adds or removes a permission to a group.\n" +
                "`{%}permissions group <group> create|delete` - Creates or deletes a group.\n" +
                "`{%}permissions group <group> link <role>` - Links the group to a discord role.\n" +
                "`{%}permissions group <group> unlink` - Unlinks the group from a role.\n" +
                "`{%}permissions group <group> list [page]` - lists the permissions this group has.\n" +
                "`{%}permissions group <group> massadd <@everyone/@here/role>` - Puts everyone with the giving role into the group.\n" +
                "`{%}permissions group <group> clear` - Removes all permissions from this group!\n" +
                "`{%}permissions group <group> move <pos>` - Moves the group to a different position on the hierarchy.\n" +
                "`{%}permissions group <group> clone <new_group>` - Clones a group.\n" +
                "`{%}permissions group <group> rename <new_name>` - Renames the group.\n\n" +
                "`{%}permissions user <user> group add|remove <group>` - Adds or removes a group from this user.\n" +
                "`{%}permissions user <user> group list [page]` - Lists the groups this user is in.\n" +
                "`{%}permissions user <user> permission add|remove <perm>` - Adds or removes a permissions from this user.\n" +
                "`{%}permissions user <user> permission list [page]` - list the permmissions this user has (Excluding those obtained from groups).\n" +
                "`{%}permissions user <user> check` - Returns all permissions a user has access to\n" +
                "`{%}permissions user <user> clear` - Clears all user specific permissions from the specified user!\n\n" +
                "`{%}permissions groups` - Lists all the groups in a server.\n" +
                "`{%}permissions list` - Lists all the permissions for FlareBot!\n" +
                "`{%}permissions reset` - Resets all of the guilds perms.";
    }

    @Override
    public Permission getPermission() {
        return Permission.PERMISSIONS_COMMAND;
    }


    @Override
    public CommandType getType() {
        return CommandType.MODERATION;
    }

}
