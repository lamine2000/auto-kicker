package main;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.time.Duration;
import java.time.Instant;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        if(args.length < 1){
            System.out.println("Missing argument : token");
            System.exit(1);
        }

        final String token = args[0];
        JDABuilder.create(token,
                        GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS_AND_STICKERS)

                .addEventListeners(new Bot())
                .setActivity(Activity.watching("You, haha !!"))
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("API is ready!");
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
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(String.format("Greetings, %s!!", event.getUser().getAsTag()),  event.getUser().getEffectiveAvatarUrl());
        eb.setColor(new Color(0xf44336));
        eb.setDescription("Here are our rules:\n1- Don't blala\n2- Try and bleble first\n3- Have fun !");
        eb.setAuthor("The Moderators", "https://i.imgur.com/g4awqas.jpeg");
        eb.setFooter("Have fun !", "https://i.imgur.com/g4awqas.jpeg");
        eb.setImage("https://i.imgur.com/g4awqas.jpeg");
        eb.setThumbnail("https://i.imgur.com/g4awqas.jpeg");
        eb.setTimestamp(Instant.now());

        event.getGuild().getTextChannels().get(0)
                .sendMessageEmbeds(eb.build()).queue(
                        message ->
                                message.addReaction(Emoji.fromUnicode("U+1F44C")).queue()
                );

        Scheduler scheduler = new Scheduler();
        scheduler.schedule(
                () ->
                    event.getGuild().kick(event.getUser(), "Unassigned for too long !").queue(),
                    Schedules.fixedDelaySchedule(Duration.ofMinutes(2))
        );
    }
}