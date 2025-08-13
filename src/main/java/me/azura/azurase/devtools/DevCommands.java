package me.azura.azurase.devtools;

import me.azura.azurase.core.command.BaseCommand;
import me.azura.azurase.core.text.Text;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.URI;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class DevCommands {
	private static final OkHttpClient HTTP = new OkHttpClient.Builder().callTimeout(Duration.ofSeconds(10)).build();

	public static final class Performance extends BaseCommand {
		@Override protected String permission() { return "azura.dev.performance"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			double[] tps = Bukkit.getTPS();
			long free = Runtime.getRuntime().freeMemory(); long total = Runtime.getRuntime().totalMemory();
			Text.sendPrefixed(sender, "<gray>TPS: "+fmt(tps)+" RAM: "+((total-free)/1024/1024)+"/"+(total/1024/1024)+"MB Online: "+Bukkit.getOnlinePlayers().size());
			return true; }
		private String fmt(double[] tps) { return String.format(Locale.US, "%.2f %.2f %.2f", tps[0], tps[1], tps[2]); }
	}
	public static final class Debug extends BaseCommand {
		private final DevToolsService svc; public Debug(DevToolsService s) { this.svc = s; }
		@Override protected String permission() { return "azura.dev.debug"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<2) { Text.sendPrefixed(sender, "<red>Usage: /debug <service> <on|off>"); return true; }
			svc.setDebug(args[0], args[1].equalsIgnoreCase("on")); Text.sendPrefixed(sender, "<green>Debug set."); return true; }
	}
	public static final class Enumerated extends BaseCommand {
		private final DevToolsService svc; public Enumerated(DevToolsService s) { this.svc = s; }
		@Override protected String permission() { return "azura.dev.enumerated"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<2) { Text.sendPrefixed(sender, "<red>Usage: /enumerated <id> <true|false>"); return true; }
			svc.setFeature(args[0], Boolean.parseBoolean(args[1])); Text.sendPrefixed(sender, "<green>Feature flag set."); return true; }
	}
	public static final class Scale extends BaseCommand {
		@Override protected String permission() { return "azura.dev.scale"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Scaling not applicable in prototype."); return true; }
	}
	public static final class Binary extends BaseCommand {
		@Override protected String permission() { return "azura.dev.binary"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Binary sandbox not enabled."); return true; }
	}
	public static final class Spy extends BaseCommand {
		private final DevToolsService svc; public Spy(DevToolsService s) { this.svc = s; }
		@Override protected String permission() { return "azura.dev.spy"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players."); return true; }
			if (args.length>=1 && args[0].equalsIgnoreCase("packets")) { svc.toggleSpyPackets(p.getUniqueId()); Text.sendPrefixed(p, "<green>Packet spy toggled."); return true; }
			svc.toggleSpyCommands(p.getUniqueId()); Text.sendPrefixed(p, "<green>Command spy toggled."); return true; }
	}
	public static final class Patest extends BaseCommand {
		@Override protected String permission() { return "azura.dev.patest"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /patest <placeholder>"); return true; }
			String token = args[0];
			String result = org.bukkit.Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") ? me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(sender instanceof Player p? p:null, token) : "PAPI not installed";
			Text.sendPrefixed(sender, "<gray>"+result);
			return true; }
	}
	public static final class HttpGet extends BaseCommand {
		@Override protected String permission() { return "azura.dev.httpget"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /httpget <url>"); return true; }
			try (Response resp = HTTP.newCall(new Request.Builder().url(args[0]).build()).execute()) {
				Text.sendPrefixed(sender, "<gray>"+resp.code()+" " + Objects.toString(resp.header("Content-Length"), "?")+"B in "+Objects.toString(resp.header("Date"), "-") );
			} catch (IOException e) { Text.sendPrefixed(sender, "<red>"+e.getMessage()); }
			return true; }
	}
	public static final class Wsdebug extends BaseCommand {
		@Override protected String permission() { return "azura.dev.wsdebug"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>WS debug not implemented."); return true; }
	}
	public static final class ApiProfile extends BaseCommand {
		@Override protected String permission() { return "azura.dev.apiprofile"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>API profile not implemented."); return true; }
	}
	public static final class DbQuery extends BaseCommand {
		@Override protected String permission() { return "azura.dev.dbquery"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>DB query is disabled (read-only stub)."); return true; }
	}
	public static final class Cache extends BaseCommand {
		@Override protected String permission() { return "azura.dev.cache"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Cache listing available in future."); return true; }
	}
	public static final class Serialize extends BaseCommand {
		@Override protected String permission() { return "azura.dev.serialize"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Serialize sandbox disabled."); return true; }
	}
	public static final class MemorySnap extends BaseCommand {
		@Override protected String permission() { return "azura.dev.memorysnap"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			long free = Runtime.getRuntime().freeMemory(); long total = Runtime.getRuntime().totalMemory();
			Text.sendPrefixed(sender, "<gray>Memory: used "+((total-free)/1024/1024)+"MB / total "+(total/1024/1024)+"MB"); return true; }
	}
	public static final class CmdWatch extends BaseCommand {
		private final DevToolsService svc; public CmdWatch(DevToolsService s) { this.svc = s; }
		@Override protected String permission() { return "azura.dev.cmdwatch"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players."); return true; }
			if (args.length<1) { svc.setCmdWatch(p.getUniqueId(), null); Text.sendPrefixed(p, "<yellow>Stopped watching commands."); return true; }
			Player target = Bukkit.getPlayerExact(args[0]); if (target==null) { Text.sendPrefixed(p, "<red>Player not found."); return true; }
			svc.setCmdWatch(p.getUniqueId(), target.getUniqueId()); Text.sendPrefixed(p, "<green>Watching commands from "+target.getName()+"."); return true; }
	}
	public static final class FileCheck extends BaseCommand {
		@Override protected String permission() { return "azura.dev.filecheck"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>File check stub."); return true; }
	}
	public static final class TimeMethod extends BaseCommand {
		@Override protected String permission() { return "azura.dev.timemethod"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Time method stub."); return true; }
	}
	public static final class Flow extends BaseCommand {
		@Override protected String permission() { return "azura.dev.flow"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Flow visualization not implemented."); return true; }
	}
	public static final class ErrorLog extends BaseCommand {
		@Override protected String permission() { return "azura.dev.errorlog"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Error replay stub."); return true; }
	}
	public static final class Threads extends BaseCommand {
		@Override protected String permission() { return "azura.dev.threads"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			ThreadMXBean mx = ManagementFactory.getThreadMXBean();
			ThreadInfo[] infos = mx.dumpAllThreads(true, true);
			Text.sendPrefixed(sender, "<gray>Threads: "+infos.length);
			return true; }
	}
	public static final class GcStats extends BaseCommand {
		@Override protected String permission() { return "azura.dev.gcstats"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { System.gc(); Text.sendPrefixed(sender, "<green>GC triggered."); return true; }
	}
	public static final class JvmFlags extends BaseCommand {
		@Override protected String permission() { return "azura.dev.jvmflags"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<gray>"+String.join(" ", ManagementFactory.getRuntimeMXBean().getInputArguments())); return true; }
	}
	public static final class PluginDep extends BaseCommand {
		@Override protected String permission() { return "azura.dev.plugindep"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /plugindep <plugin>"); return true; }
			Plugin p = Bukkit.getPluginManager().getPlugin(args[0]); if (p==null) { Text.sendPrefixed(sender, "<red>Plugin not found."); return true; }
			Text.sendPrefixed(sender, "<gray>Depends: "+String.join(", ", p.getDescription().getDepend()));
			return true; }
	}
	public static final class ClassLoad extends BaseCommand {
		@Override protected String permission() { return "azura.dev.classload"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<1) { Text.sendPrefixed(sender, "<red>Usage: /classload <class>"); return true; }
			try { Class<?> c = Class.forName(args[0]); Text.sendPrefixed(sender, "<gray>Loaded by "+c.getClassLoader()); } catch (ClassNotFoundException e) { Text.sendPrefixed(sender, "<red>Not found"); }
			return true; }
	}
	public static final class InjectCode extends BaseCommand {
		@Override protected String permission() { return "azura.dev.injectcode"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<red>Disabled by config."); return true; }
	}
	public static final class Physics extends BaseCommand {
		@Override protected String permission() { return "azura.dev.physics"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Physics test disabled."); return true; }
	}
	public static final class ValidateConfig extends BaseCommand {
		@Override protected String permission() { return "azura.dev.validateconfig"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Schema validation not implemented."); return true; }
	}
	public static final class MigrateData extends BaseCommand {
		@Override protected String permission() { return "azura.dev.migratedata"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Migration stub."); return true; }
	}
	public static final class Exploit extends BaseCommand {
		@Override protected String permission() { return "azura.dev.exploit"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) { Text.sendPrefixed(sender, "<yellow>Exploit scan stub."); return true; }
	}
	public static final class Stress extends BaseCommand {
		@Override protected String permission() { return "azura.dev.stress"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (args.length<2 || !args[0].equalsIgnoreCase("entities")) { Text.sendPrefixed(sender, "<red>Usage: /stress entities <count>"); return true; }
			int count = Integer.parseInt(args[1]);
			Text.sendPrefixed(sender, "<yellow>Spawning "+count+" dummy entities is disabled for safety.");
			return true; }
	}
	public static final class Alert extends BaseCommand {
		private final DevToolsService svc; public Alert(DevToolsService s) { this.svc = s; }
		@Override protected String permission() { return "azura.dev.alert"; }
		@Override protected boolean execute(CommandSender sender, String label, String[] args) {
			if (!(sender instanceof Player p)) { Text.sendPrefixed(sender, "<red>Only players."); return true; }
			if (args.length<2 || !args[0].equalsIgnoreCase("tps")) { Text.sendPrefixed(p, "<red>Usage: /alert tps <threshold>"); return true; }
			double th = Double.parseDouble(args[1]);
			svc.addAlert(p.getUniqueId(), th); Text.sendPrefixed(p, "<green>TPS alert set at "+th+"."); return true; }
	}
}