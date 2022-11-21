package me.unleqitq.commandframework.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;

public record ComponentWrapper(BaseComponent component) {
	
	public ComponentWrapper hoverEvent(HoverEvent hoverEvent) {
		component.setHoverEvent(hoverEvent);
		return this;
	}
	
	public ComponentWrapper clickEvent(ClickEvent clickEvent) {
		component.setClickEvent(clickEvent);
		return this;
	}
	
	
}
