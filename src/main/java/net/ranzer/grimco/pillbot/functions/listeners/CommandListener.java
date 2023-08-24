package net.ranzer.grimco.pillbot.functions.listeners;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.ranzer.grimco.pillbot.PillBot;
import net.ranzer.grimco.pillbot.commands.BotCommand;
import net.ranzer.grimco.pillbot.commands.admin.HelpCommand;
import net.ranzer.grimco.pillbot.commands.admin.InfoCommand;
import net.ranzer.grimco.pillbot.commands.admin.PingCommand;
import net.ranzer.grimco.pillbot.util.Logging;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {
	private static CommandListener cl;
	private final List<BotCommand> cmds = new ArrayList<>();

	public static CommandListener getInstance() {
		if (cl == null) cl = new CommandListener();
		return cl;
	}

	private CommandListener() {
		this.addCommand(new HelpCommand())
			.addCommand(new InfoCommand())
			.addCommand(new PingCommand());

		List<CommandData> slashCmds = new ArrayList<>();
		for (BotCommand cmd : cmds) {
			if (cmd.getSlashCommandData() != null) {
				slashCmds.add(cmd.getSlashCommandData());
			}
		}

		for (Guild g : PillBot.getJDA().getGuilds()) {
			g.updateCommands().addCommands(slashCmds).queue();
		}
	}

	private CommandListener addCommand(BotCommand cmd) {
		this.cmds.add(cmd);
		return this;
	}

	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		Logging.debug("looking for a slash command");
		Optional<BotCommand> c = cmds.stream().filter(cmd -> cmd.getName().equals(event.getName())).findFirst();
		if (c.isPresent()) {
			Logging.debug("found one");
			for (OptionMapping o : event.getOptions()) {
				Logging.debug(String.format("%s: %s", o.getName(), o.getAsString()));
			}
		}
		c.ifPresent(botCommand -> callSlashCommand(event, botCommand));
	}

	private void callSlashCommand(SlashCommandInteractionEvent event, BotCommand cmd) {
		new Thread() {
			@Override
			public void run() {
				cmd.runSlashCommand(event);
				interrupt();
			}
		}.start();
	}

	public List<BotCommand> getCommands() {

		return cmds;
	}

}
