package net.ranzer.grimco.pillbot.commands;

import java.awt.*;

public enum Category {
			
	ADMIN("Admin", Color.RED),
	PILL("Pill",Color.ORANGE);

    public final String NAME;
	public final Color COLOR;
	
	Category(String name, Color color){
		NAME = name;
		COLOR =color;
	}
	
	@Override
	public String toString(){
		return NAME;
	}

}
