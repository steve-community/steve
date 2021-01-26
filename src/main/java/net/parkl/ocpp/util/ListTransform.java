package net.parkl.stevep.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListTransform {
	public static <I,T> List<T> transform(List<I> list, Function<I,T> mapper) {
		return list.stream()
				  .map(mapper)
				  .collect(Collectors.toList());
	}
	
	public static <I,K> Map<K,I> transformToMap(Iterable<I> list, Function<I,K> keyMapper) {
		Map<K, I> ret=new HashMap<>();
		for (I item:list) {
			K key = keyMapper.apply(item);
			ret.put(key,item);
		}
		return ret;
	}
	
	public static <I,K> Map<K, List<I>> transformToListMap(List<I> list, Function<I,K> keyMapper) {
		Map<K, List<I>> ret=new HashMap<>();
		for (I item:list) {
			K key = keyMapper.apply(item);
			List<I> mapList = ret.get(key);
			if (mapList==null) {
				mapList=new ArrayList<>();
				ret.put(key, mapList);
			}
			mapList.add(item);
		}
		return ret;
	}
}
