package me.azura.azurase.core.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;

public final class DatabaseManager {
	private final JavaPlugin plugin;
	private HikariDataSource dataSource;

	public DatabaseManager(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public void initializeIfMysql() {
		String type = plugin.getConfig().getString("storage.type", "YAML");
		if (!"MYSQL".equalsIgnoreCase(type)) return;
		HikariConfig cfg = new HikariConfig();
		String host = plugin.getConfig().getString("storage.mysql.host", "localhost");
		int port = plugin.getConfig().getInt("storage.mysql.port", 3306);
		String db = plugin.getConfig().getString("storage.mysql.database", "azurase");
		String user = plugin.getConfig().getString("storage.mysql.username", "root");
		String pass = plugin.getConfig().getString("storage.mysql.password", "");
		cfg.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&characterEncoding=utf8");
		cfg.setUsername(user);
		cfg.setPassword(pass);
		cfg.setMaximumPoolSize(plugin.getConfig().getInt("storage.mysql.pool.maximumPoolSize", 10));
		cfg.setMinimumIdle(plugin.getConfig().getInt("storage.mysql.pool.minimumIdle", 2));
		cfg.setConnectionTimeout(plugin.getConfig().getLong("storage.mysql.pool.connectionTimeoutMillis", 10000));
		this.dataSource = new HikariDataSource(cfg);
	}

	public DataSource getDataSource() { return dataSource; }

	public boolean isMysql() { return dataSource != null; }

	public void shutdown() {
		if (dataSource != null) {
			try { dataSource.close(); } catch (Exception ignored) {}
		}
	}
}