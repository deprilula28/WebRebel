package me.deprilula28.WebRebel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Utils{

	public static <K, V> List<Entry<K, V>> sortKeys(Map<K, V> origin, Comparator<K> comparator){
		
		List<Entry<K, V>> keys = new ArrayList<>();
		keys.addAll(origin.entrySet());
		
		Collections.sort(keys, (o1, o2) -> {
			return comparator.compare(o1.getKey(), o2.getKey());
		});
		
		return keys;
		
	}

	public static <K, V> List<Entry<K, V>> sortValues(Map<K, V> origin, Comparator<V> comparator){
		
		List<Entry<K, V>> keys = new ArrayList<>();
		keys.addAll(origin.entrySet());
		
		Collections.sort(keys, (o1, o2) -> {
			return comparator.compare(o1.getValue(), o2.getValue());
		});
		
		return keys;
		
	}
	
}
