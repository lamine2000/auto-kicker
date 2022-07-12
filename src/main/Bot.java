package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {
    @Override
    public void onGenericRole(@NotNull GenericRoleEvent event) {
        System.out.println("genric role event triggered");
        event.getGuild().getTextChannels().get(0).sendMessage("test...GenericRoleEvent triggered: " + event).queue();
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        System.out.println("event 2 triggered");
        event.getGuild().getTextChannels().get(0).sendMessage("test...GuildMemberJoinEvent triggered").queue();
    }

    public static void main(String[] args) throws LoginException {
        if(args.length < 1){
            System.out.println("Missing argument : token");
            System.exit(1);
        }

        final String token = args[0];
        JDA jda = JDABuilder.createLight(token,
                        GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES)

                .addEventListeners(new Bot())
                .setActivity(Activity.watching("You, haha !!"))
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("API is readyssss!");
    }
}