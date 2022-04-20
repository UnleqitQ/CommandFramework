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
			Field byKey = PotionEffectType.class.getDeclaredField("byKey");
			byKey.setAccessible(true);
			Map<NamespacedKey, PotionEffectType> map = (Map<NamespacedKey, PotionEffectType>) byKey.get(null);
			return new HashSet<>(map.values());
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
