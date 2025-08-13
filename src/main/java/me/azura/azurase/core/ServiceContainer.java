package me.azura.azurase.core;

import me.azura.azurase.clans.ClanCommand;
import me.azura.azurase.clans.ClanListener;
import me.azura.azurase.clans.ClanService;
import me.azura.azurase.clans.storage.YamlClanRepository;
import me.azura.azurase.core.command.AzuraEngineCommand;
import me.azura.azurase.core.command.BroadcastCommand;
import me.azura.azurase.core.command.PingCommand;
import me.azura.azurase.core.config.ConfigManager;
import me.azura.azurase.core.listener.BackTrackerListener;
import me.azura.azurase.core.listener.CommandBlockListener;
import me.azura.azurase.homes.HomesCommands;
import me.azura.azurase.homes.HomesService;
import me.azura.azurase.moderation.ModerationCommands;
import me.azura.azurase.moderation.ModerationListener;
import me.azura.azurase.moderation.ModerationService;
import me.azura.azurase.rtp.RtpCommand;
import me.azura.azurase.rtp.RtpService;
import me.azura.azurase.spawn.SpawnCommands;
import me.azura.azurase.spawn.SpawnService;
import me.azura.azurase.teleport.TeleportService;
import me.azura.azurase.teleport.commands.SimpleTeleportCommands;
import me.azura.azurase.teleport.commands.TpaCommands;
import me.azura.azurase.vanish.VanishCommand;
import me.azura.azurase.vanish.VanishListener;
import me.azura.azurase.vanish.VanishService;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public final class ServiceContainer implements Services {
	private final AzuraSEPlugin plugin;
	private final ScheduledThreadPoolExecutor asyncExecutor;
	private final List<Listener> listeners;

	private ConfigManager configManager;
	private VanishService vanishService;
	private TeleportService teleportService;
	private SpawnService spawnService;
	private HomesService homesService;
	private ClanService clanService;
	private ModerationService moderationService;
	private RtpService rtpService;

	public ServiceContainer(AzuraSEPlugin plugin) {
		this.plugin = plugin;
		this.listeners = new ArrayList<>();
		this.asyncExecutor = new ScheduledThreadPoolExecutor(
			Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
			new NamedThreadFactory("AzuraSE-Async")
		);
		this.asyncExecutor.setKeepAliveTime(60, TimeUnit.SECONDS);
		this.asyncExecutor.allowCoreThreadTimeOut(true);
	}

	public void initialize() {
		this.configManager = new ConfigManager(plugin);
		this.configManager.loadAll();
		this.vanishService = new VanishService(plugin);
		this.teleportService = new TeleportService();
		this.spawnService = new SpawnService(plugin);
		this.homesService = new HomesService(plugin);
		this.clanService = new ClanService(new YamlClanRepository(plugin));
		this.moderationService = new ModerationService(plugin);
		this.rtpService = new RtpService(plugin);
	}

	public void registerCommandsAndListeners() {
		bindCommand("azuraengine", new AzuraEngineCommand(plugin, configManager));
		bindCommand("vanish", new VanishCommand(vanishService));
		registerListener(new VanishListener(vanishService));

		bindCommand("tp", new SimpleTeleportCommands.Tp(teleportService));
		bindCommand("tphere", new SimpleTeleportCommands.Tphere(teleportService));
		bindCommand("back", new SimpleTeleportCommands.Back(teleportService));
		registerListener(new BackTrackerListener(teleportService));

		bindCommand("tpa", new TpaCommands.Tpa(teleportService, plugin.getConfig().getInt("teleport.tpa-expiry-seconds", 60)));
		bindCommand("tpahere", new TpaCommands.Tpahere(teleportService, plugin.getConfig().getInt("teleport.tpa-expiry-seconds", 60)));
		bindCommand("tpaccept", new TpaCommands.Tpaccept(teleportService));
		bindCommand("tpdeny", new TpaCommands.Tpdeny(teleportService));

		bindCommand("spawn", new SpawnCommands.Spawn(spawnService));
		bindCommand("setspawn", new SpawnCommands.SetSpawn(spawnService));

		bindCommand("home", new HomesCommands.Home(homesService));
		bindCommand("sethome", new HomesCommands.Sethome(homesService));
		bindCommand("delhome", new HomesCommands.Delhome(homesService));

		bindCommand("broadcast", new BroadcastCommand());
		bindCommand("ping", new PingCommand());

		bindCommand("clan", new ClanCommand(clanService));
		registerListener(new ClanListener(clanService));

		bindCommand("fly", new ModerationCommands.Fly());
		bindCommand("heal", new ModerationCommands.Heal());
		bindCommand("feed", new ModerationCommands.Feed());
		bindCommand("gm", new ModerationCommands.Gm());
		bindCommand("kick", new ModerationCommands.Kick());
		bindCommand("ban", new ModerationCommands.Ban());
		bindCommand("tempban", new ModerationCommands.Tempban());
		bindCommand("mute", new ModerationCommands.Mute(moderationService));
		bindCommand("unmute", new ModerationCommands.Unmute(moderationService));
		bindCommand("warn", new ModerationCommands.Warn());
		bindCommand("invsee", new ModerationCommands.Invsee());
		bindCommand("clear", new ModerationCommands.Clear());
		bindCommand("staffchat", new ModerationCommands.Staffchat(moderationService));
		registerListener(new ModerationListener(moderationService));

		bindCommand("rtp", new RtpCommand(rtpService));

		registerListener(new CommandBlockListener(plugin));
	}

	public void shutdown() {
		try {
			this.asyncExecutor.shutdownNow();
		} catch (Exception ignored) {}
	}

	public ScheduledThreadPoolExecutor getAsyncExecutor() {
		return asyncExecutor;
	}

	public void bindCommand(String name, Object executorTabCompleter) {
		PluginCommand cmd = plugin.getCommand(name);
		if (cmd != null) {
			if (executorTabCompleter instanceof org.bukkit.command.CommandExecutor ce) {
				cmd.setExecutor(ce);
			}
			if (executorTabCompleter instanceof org.bukkit.command.TabCompleter tc) {
				cmd.setTabCompleter(tc);
			}
		}
	}

	public void registerListener(Listener listener) {
		this.listeners.add(listener);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	@Override
	public VanishService vanish() {
		return vanishService;
	}

	private static final class NamedThreadFactory implements ThreadFactory {
		private final String baseName;
		private volatile int idx = 0;
		NamedThreadFactory(String baseName) {
			this.baseName = baseName;
		}
		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, baseName + "-" + (++idx));
			t.setDaemon(true);
			return t;
		}
	}
}