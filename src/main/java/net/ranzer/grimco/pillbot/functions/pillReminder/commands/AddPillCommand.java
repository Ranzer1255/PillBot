package net.ranzer.grimco.pillbot.functions.pillReminder.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.pillbot.commands.BotCommand;
import net.ranzer.grimco.pillbot.commands.Describable;
import net.ranzer.grimco.pillbot.functions.pillReminder.PillReminderManager;
import net.ranzer.grimco.pillbot.util.StringUtil;

import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesException;

public class AddPillCommand extends BotCommand implements Describable {

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		MessageChannel channel =event.getChannel();
		Member caller = event.getMember();
		String name = event.getOption("name", OptionMapping::getAsString);
		String TimeInput = event.getOption("time",OptionMapping::getAsString);
		String dose = event.getOption("dose","",OptionMapping::getAsString);

		ZonedDateTime time;
		try {
			time = StringUtil.parseTimeWithTimeZone(TimeInput);
		} catch (ZoneRulesException zre){
			event.reply("I'm sorry but i didn't understand the timezone. i got the error "+zre.getMessage() +
			            " This is likely caused by Capitalization or a space. the I tried to make the code as user " +
			            "friendly as possible, but Java is persnickety about Timezones. Acceptable Timezones include: \n" +
			            "America/New_York\n" +
			            "US/Central\n" +
			            "GMT\n" +
			            "UTC+3").setEphemeral(true).queue();
			return;
		}
		if(time==null){
			event.reply("I'm sorry, i didn't understand \""+TimeInput+"\"\n" +
			            "Acceptable inputs include:\n" +
			            "10p\n" +
			            "5:15 am\n" +
			            "21:20\n" +
			            "3:45p America/Chicago").setEphemeral(true).queue();
			return;
		}

		//todo temporary UI test reply. remove this and actually use the parsed data to create a reminder
//		event.reply(String.format("pasred data: \ncaller: %s\nName: %s\n Time: %s\nDose: %s",caller.getAsMention(),name,time.toString(),dose)).queue();

		PillReminderManager.getInstance().schedulePillReminder(name,time,channel,dose,caller);
		event.reply("reminder added").setEphemeral(true).queue();
	}

	@Override
	public String getName() {
		return "add-pill";
	}

	@Override
	public String getShortDescription() {
		return "setup a single daily reminder and a specified time";
	}

	@Override
	public String getLongDescription() {
		return getShortDescription()+"\n\n" +
		       "This reminder will go off every 24 hours starting at the next specified time";
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		SlashCommandData rtn = Commands.slash(getName(),getShortDescription());

		rtn.addOption(OptionType.STRING,"name","Name of this reminder",true);
		rtn.addOption(OptionType.STRING,"time","when should this reminder happen?",true);
		rtn.addOption(OptionType.STRING,"dose","(optional) dosage of this item?",false);

		return rtn;
	}

	@Override
	protected boolean isApplicableToPM() {
		return false;
	}
}
