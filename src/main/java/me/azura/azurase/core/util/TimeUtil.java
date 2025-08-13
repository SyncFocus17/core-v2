package me.azura.azurase.core.util;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {
	private static final Pattern DURATION = Pattern.compile("(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?", Pattern.CASE_INSENSITIVE);
	private TimeUtil() {}

	public static Duration parseDuration(String input) {
		if (input == null || input.isEmpty()) return Duration.ZERO;
		Matcher m = DURATION.matcher(input);
		if (!m.matches()) return Duration.ZERO;
		long days = get(m, 1);
		long hours = get(m, 2);
		long minutes = get(m, 3);
		long seconds = get(m, 4);
		return Duration.ofDays(days).plusHours(hours).plusMinutes(minutes).plusSeconds(seconds);
	}

	private static long get(Matcher m, int group) {
		String v = m.group(group);
		return v == null || v.isEmpty() ? 0L : Long.parseLong(v);
	}

	public static String formatShort(Duration d) {
		long seconds = d.getSeconds();
		long days = seconds / 86400; seconds %= 86400;
		long hours = seconds / 3600; seconds %= 3600;
		long minutes = seconds / 60; seconds %= 60;
		StringBuilder sb = new StringBuilder();
		if (days > 0) sb.append(days).append('d');
		if (hours > 0) sb.append(hours).append('h');
		if (minutes > 0) sb.append(minutes).append('m');
		sb.append(seconds).append('s');
		return sb.toString();
	}
}