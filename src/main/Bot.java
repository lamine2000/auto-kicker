package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GenericGuildMemberEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.role.GenericRoleEvent;
import net.dv8tion.jda.api.events.role.update.GenericRoleUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter {

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        String user_tag = event.getUser().getAsMention();

        event.getGuild().getTextChannels().get(0)
                .sendMessage(String.format(
                        "test...%s has been assigned a new role: %s",
                        user_tag,
                        event.getRoles().get(0).getName())
                ).queue();
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
        JDABuilder.createLight(token,
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