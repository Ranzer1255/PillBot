package net.ranzer.grimco.pillbot.commands;

import java.util.List;

public interface Describable {

	Category getCategory();
	
	String getName();
	String getUsage();
	String getShortDescription();
	String getLongDescription();
	boolean hasSubcommands();
	List<BotCommand> getSubcommands();
}
