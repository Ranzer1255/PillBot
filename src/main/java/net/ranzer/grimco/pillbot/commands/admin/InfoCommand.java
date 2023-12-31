package net.ranzer.grimco.pillbot.commands.admin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.pillbot.PillBot;
import net.ranzer.grimco.pillbot.commands.BotCommand;
import net.ranzer.grimco.pillbot.commands.Category;
import net.ranzer.grimco.pillbot.commands.Describable;
import net.ranzer.grimco.pillbot.config.BotConfiguration;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class InfoCommand extends BotCommand implements Describable{

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		EmbedBuilder eb;
		User bot = event.getJDA().getSelfUser();

		// 1 argument !info stats
		if (event.getOption("stats")!=null) {
			eb = statusEmbed(bot);
		}
		// no arguments !info
		else {
			eb = infoEmbed(bot);
		}

		//color the embed
		if (event.isFromGuild()) {
			Member m = event.getGuild().retrieveMember(bot).complete();
			eb.setColor(m.getColor());
		}

		event.replyEmbeds(eb.build()).queue();
	}

//	@Override
//	public void processPrefix(String[] args, MessageReceivedEvent event) {
//
//		EmbedBuilder eb;
//		User bot = event.getJDA().getSelfUser();
//
//		// 1 argument !info stats
//		if (args.length == 1 && args[0].equals("stats")) {
//			eb = statusEmbed(bot);
//		}
//		// no arguments !info
//		else {
//			eb = infoEmbed(bot);
//		}
//
//		//color the embed
//		if (event.isFromGuild()) {
//			Member m = event.getGuild().retrieveMember(bot).complete();
//			eb.setColor(m.getColor());
//		}
//
//		event.getChannel().sendMessageEmbeds(eb.build()).queue();
//
//	}

	static private EmbedBuilder statusEmbed(User bot) {
		EmbedBuilder rtn = coreEmbed(bot);
	//noinspection ConstantConditions
		rtn.addField("Guilds", String.valueOf(bot.getJDA().getGuilds().size()), false)
		  .addField("Users", countNonBotUsers(bot.getJDA()), true)
		  .addField("Bots", countBotUsers(bot.getJDA()), true)
		  .addField("Up Time",getUpTime(), true)
		  .addField("Game", bot.getJDA().getPresence().getActivity().getName(), true);
		return rtn;
	}

	static private EmbedBuilder infoEmbed(User bot) {
		EmbedBuilder rtn = coreEmbed(bot);
		  rtn.addField("Version", BotConfiguration.getInstance().getVersion(), true)
		  .addField("Language", "Java", true)
		  .addField("Artwork", "[Delapouite](https://game-icons.net/1x1/delapouite/ticket.html)", false)
//		  .addField("Invite me!", inviteLinkBuilder(bot), true)
		  .addField("GitHub Repo", "[GitHub](https://github.com/Ranzer1255/GrimcoRaffleBot)\n[Bugs and Suggestions](https://github.com/Ranzer1255/GrimcoRaffleBot/issues)", true)
		  .setFooter("Please report bugs or suggestions in the link above", null);
		return rtn;
	}

	static private EmbedBuilder coreEmbed(User bot) {
		EmbedBuilder rtn = new EmbedBuilder();
		rtn.setAuthor(bot.getName(), "https://github.com/Ranzer1255/GrimcoRaffleBot", bot.getAvatarUrl())
		  .setTitle("Raffle Bot for TLoG",null)
		  .setDescription("Written by Ranzer")
		  .setThumbnail(bot.getAvatarUrl());
		return rtn;
	}

	@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
	private static String getUpTime() {
		StringBuilder sb = new StringBuilder();
		LocalDateTime now = LocalDateTime.now();
		
		if(PillBot.START_TIME.until(now, ChronoUnit.YEARS) != 0){
			sb.append(PillBot.START_TIME.until(now, ChronoUnit.YEARS) + " Yrs, ");
			now=now.minusYears(PillBot.START_TIME.until(now, ChronoUnit.YEARS));
		}
		if(PillBot.START_TIME.until(now, ChronoUnit.MONTHS) != 0){
			sb.append(PillBot.START_TIME.until(now, ChronoUnit.MONTHS) + " Mths, ");
			now=now.minusMonths(PillBot.START_TIME.until(now, ChronoUnit.MONTHS));
		}
		if(PillBot.START_TIME.until(now, ChronoUnit.DAYS) != 0){
			sb.append(PillBot.START_TIME.until(now, ChronoUnit.DAYS) + " Days, ");
			now=now.minusDays(PillBot.START_TIME.until(now, ChronoUnit.DAYS));
		}
		if(PillBot.START_TIME.until(now, ChronoUnit.HOURS) != 0){
			sb.append(PillBot.START_TIME.until(now, ChronoUnit.HOURS) + " Hrs, ");
			now=now.minusHours(PillBot.START_TIME.until(now, ChronoUnit.HOURS));
		}
		if(PillBot.START_TIME.until(now, ChronoUnit.MINUTES) != 0){
			sb.append(PillBot.START_TIME.until(now, ChronoUnit.MINUTES) + " Mins, ");
			now=now.minusMinutes(PillBot.START_TIME.until(now, ChronoUnit.MINUTES));
		}
		if(PillBot.START_TIME.until(now, ChronoUnit.SECONDS) != 0){
			sb.append(PillBot.START_TIME.until(now, ChronoUnit.SECONDS) + " Secs, ");
		}
		
		
		sb.delete(sb.length()-2, sb.length());
		sb.append(".");
		
		return sb.toString();
	}

	private static String countBotUsers(JDA api) {
		int count = 0;
		
		for(User u:api.getUsers()){
			if (u.isBot()){
				count++;
			}
		}
		
		return String.valueOf(count);
	}

	private static String countNonBotUsers(JDA api) {
		int count = 0;
		
		for(User u:api.getUsers()){
			if (!u.isBot()){
				count++;
			}
		}
		
		return String.valueOf(count);
	}

	@Override
	public String getUsage() {
		return String.format("/`%s [stats]`",getName());

	}

	@Override
	public String getName() {
		return "info";
	}

	@Override
	public String getShortDescription() {

		return "Information about GrimcoAi and Author";
	}

	@Override
	public Category getCategory() {
		return Category.ADMIN;
	}

	@Override
	public String getLongDescription() {
		return """
				This command gives detailed information about the bot

				`stats`: displays misc. stats reported by JDA""";
	}

	@Override
	public boolean isApplicableToPM() {
		return true;
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());
		rtn.addOption(OptionType.STRING,"stats", "do you want the stats?");

		return rtn;
	}
}
