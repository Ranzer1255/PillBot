package net.ranzer.grimco.pillbot.functions.pillReminder.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.pillbot.commands.BotCommand;

import java.util.Date;

public class PillReminderSetupCommand extends BotCommand {

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {
		Date startTime = new Date();
		event.deferReply().queue(reply->{
			Date endTime = new Date();
			long lag = endTime.getTime()-startTime.getTime();
			reply.editOriginal("pong! `"+lag+"ms`").queue();
		});
	}

	@Override
	public String getName() {
		return "setup-pill";
	}

	@Override
	public SlashCommandData getSlashCommandData() {
		return null;
	}

	@Override
	protected boolean isApplicableToPM() {
		return false;
	}
}
