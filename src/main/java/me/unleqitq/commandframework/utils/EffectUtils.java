package me.unleqitq.commandframework.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EffectUtils {
	
	public static Set<PotionEffectType> getEffectTypes() {
		try {
			Field byName = PotionEffectType.class.getDeclaredField("byName");
			byName.setAccessible(true);
			Map<String, PotionEffectType> map = (Map<String, PotionEffectType>) byName.get(null);
			return new HashSet<>(map.values());
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
