package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.Objects;

public class Bot extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        if(args.length < 1){
            System.out.println("Missing argument : token");
            System.exit(1);
        }

        final String token = args[0];
        JDA jda = JDABuilder.createLight(token,
                GatewayIntent.GUILD_BANS,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.GUILD_MESSAGES)

                .addEventListeners(new Bot())
                .setActivity(Activity.watching("You, haha !!"))
                .build();

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("ban", "Ban a user from this server. Requires permission to ban users")
                        .addOptions(new OptionData(OptionType.USER, "user", "The user to ban")
                                .setRequired(true))
                        .addOptions(new OptionData(OptionType.INTEGER, "delay_days", "Delete messages from the past days")
                                .setRequiredRange(0, 7))
                        .addOptions(new OptionData(OptionType.STRING, "reason", "The ban reason to use (default: Banned bu <user>)"))
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(
                                Permission.BAN_MEMBERS,
                                Permission.KICK_MEMBERS,
                                Permission.MESSAGE_MANAGE))
        );
        commands.queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("API is ready!");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getGuild() == null)
            return;
        if(event.getName().equals("ban")) {
            Member member = Objects.requireNonNull(event.getOption("user")).getAsMember();
            User user = Objects.requireNonNull(event.getOption("user")).getAsUser();

            ban(event, user, member);
        }

        else
            event.reply("I can't handle that command right now :(").setEphemeral(true).queue();
    }

    public void ban(SlashCommandInteractionEvent event, User user, Member member)
    {
        event.deferReply(true).queue(); // Let the user know we received the command before doing anything else
        InteractionHook hook = event.getHook(); // This is a special webhook that allows you to send messages without having permissions in the channel and also allows ephemeral messages
        hook.setEphemeral(true); // All messages here will now be ephemeral implicitly
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {
            hook.sendMessage("You do not have the required permissions to ban users from this server.").queue();
            return;
        }

        Member selfMember = Objects.requireNonNull(event.getGuild()).getSelfMember();
        if (!selfMember.hasPermission(Permission.BAN_MEMBERS)) {
            hook.sendMessage("I don't have the required permissions to ban users from this server.").queue();
            return;
        }

        if (member != null && !selfMember.canInteract(member)) {
            hook.sendMessage("This user is too powerful for me to ban.").queue();
            return;
        }

        // optional command argument, fall back to 0 if not provided
        int delDays = event.getOption("del_days", 0, OptionMapping::getAsInt); // this last part is a method reference used to "resolve" the option value

        // optional ban reason with a lazy evaluated fallback (supplier)
        String reason = event.getOption("reason",
                () -> "Banned by " + event.getUser().getAsTag(), // used if getOption("reason") is null (not provided)
                OptionMapping::getAsString); // used if getOption("reason") is not null (provided)

        // Ban the user and send a success response
        event.getGuild().ban(user, delDays, reason)
                .reason(reason) // audit-log reason
                .flatMap(v -> hook.sendMessage("Banned user " + user.getAsTag()))
                .queue();
    }
}