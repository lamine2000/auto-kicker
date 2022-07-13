package main;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;
import timer.TimerImpl;

import javax.security.auth.login.LoginException;
import java.util.Timer;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        if(args.length < 1){
            System.out.println("Missing argument : token");
            System.exit(1);
        }

        final String token = args[0];
        JDABuilder.create(token,
                        GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)

                .addEventListeners(new Bot())
                .setActivity(Activity.watching("You, haha !!"))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("API is readyssss!");
    }

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        String user_mention = event.getUser().getAsMention();

        event.getGuild().getTextChannels().get(0)
                .sendMessage(String.format(
                        "Yaaay !!! %s has been assigned a new role: %s",
                        user_mention,
                        event.getRoles().get(0).getAsMention())
                ).queue();
        TimerImpl ti = new TimerImpl("task1");

        ti.run();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        String user_mention = event.getUser().getAsMention();

        event.getGuild().getTextChannels().get(0)
                .sendMessage(String.format(
                        "Yaaay, a new member !!! Welcome %s",
                        user_mention)
                ).queue();
    }
}