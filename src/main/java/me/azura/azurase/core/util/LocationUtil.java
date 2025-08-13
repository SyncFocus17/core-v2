package me.azura.azurase.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationUtil {
	private LocationUtil() {}

	public static String serialize(Location loc) {
		return loc.getWorld().getName()+","+loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getYaw()+","+loc.getPitch();
	}

	public static Location deserialize(String s) {
		String[] p = s.split(",");
		World w = Bukkit.getWorld(p[0]);
		if (w == null) return null;
		double x = Double.parseDouble(p[1]);
		double y = Double.parseDouble(p[2]);
		double z = Double.parseDouble(p[3]);
		float yaw = Float.parseFloat(p[4]);
		float pitch = Float.parseFloat(p[5]);
		return new Location(w, x, y, z, yaw, pitch);
	}
}