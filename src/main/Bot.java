package main;

import dao.AutoKickerDAO;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Bot extends ListenerAdapter {

    public static void main(String[] args) throws LoginException {
        if(args.length < 1){
            System.out.println("Missing argument : token");
            System.exit(1);
        }

        final String token = args[0];
        JDABuilder.create(token,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_EMOJIS_AND_STICKERS)

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
        String user_name = event.getUser().getName();

        event.getGuild().getTextChannels().get(0)
                .sendMessage(String.format(
                        "Yaaay !!! %s has been assigned a new role: %s",
                        user_name,
                        event.getRoles().get(0).getAsMention())
                ).queue();

        //remove user from the autokick database when the user get assigned their firs role
        try {
            String userId = event.getUser().getId();
            String guildId = event.getGuild().getId();

            AutoKickerDAO dao = new AutoKickerDAO("membersToKick");
            dao.removeMember(userId, guildId);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        EmbedBuilder eb;
        try {
            eb = greetingsEmbedBuilder(event.getMember());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        event.getGuild().getTextChannels().get(0)
                .sendMessageEmbeds(eb.build()).queue(
                        message ->
                                message.addReaction(Emoji.fromUnicode("U+1F44C")).queue()
                );

        //register the new user to the database
        try {
            String userId = event.getUser().getId();
            String guildId = event.getGuild().getId();

            AutoKickerDAO dao = new AutoKickerDAO("membersToKick");
            dao.addNewMember(userId, guildId);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }

    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        //remove user from the autokick database when they the guild (kick, ban, leave)

        try {
            String userId = event.getUser().getId();
            String guildId = event.getGuild().getId();

            AutoKickerDAO dao = new AutoKickerDAO("membersToKick");
            dao.removeMember(userId, guildId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static EmbedBuilder greetingsEmbedBuilder(Member member) throws IOException {
        EmbedBuilder builder = new EmbedBuilder();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatedCreationDate = df.format(member.getGuild().getTimeCreated());
        Guild guild = member.getGuild();

        builder.setTitle(String.format(
                "**Greetings, %s!!**",
                member.getUser().getAsTag()
                ),
                member.getUser().getEffectiveAvatarUrl());
        builder.setColor(new Color(0xf44336));
        builder.setAuthor("The Moderators");

        builder.setDescription("\nHere are our rules:\n1- Don't blala\n2- Try and bleble first\n3- Have fun !");

        builder.addField(":busts_in_silhouette:Members", String.valueOf(guild.getMemberCount()), true);
        builder.addBlankField(true);
        builder.addField(":date:Created", formatedCreationDate, true);
        builder.addField(":crown:Owner", Objects.requireNonNull(guild.getOwner()).getAsMention(), true);
        builder.addBlankField(true);
        builder.addField(":tada:Boosters", String.valueOf(guild.getBoostCount()), true);
        builder.addBlankField(true);
        builder.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        builder.setFooter("Have fun !");
        builder.setTimestamp(Instant.now());

        return builder;
    }
}