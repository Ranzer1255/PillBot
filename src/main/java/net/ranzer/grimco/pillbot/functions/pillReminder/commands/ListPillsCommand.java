package net.ranzer.grimco.pillbot.functions.pillReminder.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.ranzer.grimco.pillbot.commands.BotCommand;
import net.ranzer.grimco.pillbot.commands.Describable;
import net.ranzer.grimco.pillbot.functions.pillReminder.PillReminderManager;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListPillsCommand extends BotCommand implements Describable {
	@Override
	protected boolean isApplicableToPM() {
		return false;
	}

	@Override
	protected void processSlash(SlashCommandInteractionEvent event) {

		List<PillReminderManager.PillReminder> pills = PillReminderManager.getInstance().getPills(event.getMember());

		StringBuilder message = new StringBuilder();
		message.append("Your current Pill list:\n");
		if (!pills.isEmpty()){
			pills.forEach(pillReminder -> {
				message.append(pillReminder).append("\n\n");
			});
		} else {
			message.append("you have none. use /add-pill to add a reminder");
		}

		event.reply(message.toString()).queue();

	}

	@Override
	public String getName() {
		return "list-pills";
	}

	@Override
	public String getShortDescription() {
		return "Lists your scheduled reminders";
	}

	@Override
	public String getLongDescription() {
		return null;
	}

}
