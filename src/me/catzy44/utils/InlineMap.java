package me.catzy44.utils;

import java.util.HashMap;
import java.util.Map;

public class InlineMap {
	@SuppressWarnings("rawtypes")
	public static Map of(Object...a ) {
		if(a.length % 2 != 0) {
			return null;
		}
		Map<Object,Object> map = new HashMap<Object,Object>();
		for(int i = 0; i < a.length; i+=2) {
			map.put(a[i], a[i+1]);
		}
		return map;
	}
}
