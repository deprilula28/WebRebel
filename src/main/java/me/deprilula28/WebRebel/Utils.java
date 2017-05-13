package me.deprilula28.WebRebel;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class Utils{

    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

    public static String sinceString(long timestamp){

        long timeDiff = System.currentTimeMillis() - timestamp;
        long second = (timeDiff / 1000) % 60;
        long minute = (timeDiff / (1000 * 60)) % 60;
        long hour = (timeDiff / (1000 * 60 * 60)) % 24;
        long day = (timeDiff / (1000 * 60 * 60 * 24)) % 30;
        long month = (timeDiff / (1000 * 60 * 60 * 24 * 30));

        if(month > 0) return month + " " + (month > 1 ? "months" : "month");
        if(day > 0) return day + " " + (day > 1 ? "days" : "day");
        if(hour > 0) return hour + " " + (hour > 1 ? "hours" : "hour");
        if(minute > 0) return minute + " " + (minute > 1 ? "minutes" : "minute");
        if(second > 0) return second + " " + (second > 1 ? "seconds" : "second");

        return timeDiff + "ms";

    }

    public static String getTimestampString(long timestamp){

        return FORMAT.format(timestamp);

    }

    public static String createIndentation(int amount){
		
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < amount; i ++) builder.append(" ");
		
		return builder.toString();
		
	}
	
	public static <K, V> List<Entry<K, V>> sortByKeys(Map<K, V> origin, Comparator<K> comparator){
		
		List<Entry<K, V>> keys = new ArrayList<>();
		keys.addAll(origin.entrySet());
		
		Collections.sort(keys, (o1, o2) -> {
			return comparator.compare(o1.getKey(), o2.getKey());
		});
		
		return keys;
		
	}

	public static <K, V> List<Entry<K, V>> sort1ByValues(Map<K, V> origin, Comparator<V> comparator){
		
		List<Entry<K, V>> keys = new ArrayList<>();
		keys.addAll(origin.entrySet());
		
		Collections.sort(keys, (o1, o2) -> {
			return comparator.compare(o1.getValue(), o2.getValue());
		});
		
		return keys;
		
	}
	
}
