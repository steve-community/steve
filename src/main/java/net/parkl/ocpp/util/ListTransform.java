/*
 * Parkl Digital Technologies
 * Copyright (C) 2020-2021
 * All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package net.parkl.ocpp.util;

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
