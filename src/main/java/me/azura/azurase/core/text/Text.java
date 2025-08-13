package me.azura.azurase.core.text;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Map;

public final class Text {
	private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
	private static volatile Component prefix = Component.empty();

	private Text() {}

	public static void setPrefix(Component newPrefix) {
		prefix = newPrefix == null ? Component.empty() : newPrefix;
	}

	public static Component parse(String mini) {
		if (mini == null || mini.isEmpty()) return Component.empty();
		return MINI_MESSAGE.deserialize(mini);
	}

	public static Component parse(String mini, Map<String, String> placeholders) {
		if (placeholders == null || placeholders.isEmpty()) {
			return parse(mini);
		}
		String resolved = mini;
		for (Map.Entry<String, String> e : placeholders.entrySet()) {
			resolved = resolved.replace("<" + e.getKey() + ">", e.getValue());
		}
		return parse(resolved);
	}

	public static void sendPrefixed(Audience audience, String mini) {
		audience.sendMessage(prefix.append(parse(mini)));
	}

	public static void send(Audience audience, String mini) {
		audience.sendMessage(parse(mini));
	}
}