package com.shizhefei.utils;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ArrayListMap<K, V> implements Map<K, V> {

	private ArrayList<Entry<K, V>> list;
	private Values v;
	private Keys k;

	public ArrayListMap() {
		super();
		list = new ArrayList<Entry<K, V>>();
	}

	/**
	 * Create a new ArrayMap with a given initial capacity.
	 */
	public ArrayListMap(int capacity) {
		super();
		list = new ArrayList<Entry<K, V>>(capacity);
	}

	/**
	 * Create a new ArrayMap with a given initial capacity.
	 */
	public ArrayListMap(Map<K, V> map) {
		super();
		list = new ArrayList<Entry<K, V>>(map.size());
		putAll(map);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return indexOfKey(key) >= 0;
	}

	@Override
	public boolean containsValue(Object value) {
		return indexOfValue(value) >= 0;
	}

	@Override
	public V get(Object key) {
		for (Entry<K, V> entry : list) {
			if (entry.getKey().equals(key)) {
				return entry.getValue();
			}
		}
		return null;
	}


	public Entry<K, V> getEntry(K key) {
		for (Entry<K, V> entry : list) {
			if (entry.key.equals(key)) {
				return entry;
			}
		}
		return null;
	}

	public V setValueAt(int index, V value) {
		Entry<K, V> entry = list.get(index);
		V oldValue = entry.value;
		entry.value = value;
		return oldValue;
	}

	public V removeAt(int index) {
		Entry<K, V> entry = list.remove(index);
		return entry.value;
	}

	@Override
	public V put(K key, V value) {
		int index = indexOfKey(key);
		if (index >= 0) {
			Entry<K, V> entry = list.get(index);
			V oldValue = entry.getValue();
			entry.setValue(value);
			return oldValue;
		}
		list.add(new Entry<K, V>(key, value));
		return null;
	}

	@Override
	public V remove(Object key) {
		Entry<K, V> find = null;
		int index = -1;
		int count = list.size();
		for (int i = 0; i < count; i++) {
			Entry<K, V> entry = list.get(i);
			if (entry.getKey().equals(key)) {
				index = i;
				find = entry;
				break;
			}
		}
		if (find != null) {
			list.remove(index);
			return find.getValue();
		}
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		list.ensureCapacity(list.size() + map.size());
		for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public Set<K> keySet() {
		return k == null ? (k = new Keys()) : k;
	}

	@Override
	public Collection<V> values() {
		return v == null ? (v = new Values()) : v;
	}

	private EntrySet e;

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return e == null ? (e = new EntrySet()) : e;
	}

	public int indexOfKey(Object key) {
		int i = 0;
		for (Entry<K, V> entry : list) {
			if (entry.getKey().equals(key)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	public int indexOfValue(Object value) {
		int i = 0;
		for (Entry<K, V> entry : list) {
			if (entry.getValue() == value) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private static class Entry<K, V> implements Map.Entry<K, V> {
		final K key;
		V value;

		/**
		 * Creates new entry.
		 */
		Entry(K k, V v) {
			value = v;
			key = k;
		}

		@Override
		public final K getKey() {
			return key;
		}

		@Override
		public final V getValue() {
			return value;
		}

		@Override
		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		@Override
		public final boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			@SuppressWarnings("rawtypes")
			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
					return true;
			}
			return false;
		}

		@Override
		public final int hashCode() {
			return h(getKey()) ^ h(getValue());
		}

		private int h(Object o) {
			return (o == null) ? 0 : o.hashCode();
		}

		@Override
		public final String toString() {
			return getKey() + "=" + getValue();
		}

	}

	private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {

		@Override
		public EntryIterator iterator() {
			return new EntryIterator(list.iterator());
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			@SuppressWarnings("unchecked")
			Map.Entry<K, V> e = (Map.Entry<K, V>) o;
			Entry<K, V> candidate = getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}

		@Override
		public int size() {
			return ArrayListMap.this.size();
		}

		@Override
		public void clear() {
			ArrayListMap.this.clear();
		}
	}

	private class EntryIterator implements Iterator<Map.Entry<K, V>> {
		Iterator<Entry<K, V>> aaa;

		public EntryIterator(Iterator<Entry<K, V>> aaa) {
			super();
			this.aaa = aaa;
		}

		@Override
		public boolean hasNext() {
			return aaa.hasNext();
		}

		@Override
		public Map.Entry<K, V> next() {
			return aaa.next();
		}

		@Override
		public void remove() {
			aaa.remove();
		}
	}

	private class ValueIterator implements Iterator<V> {
		Iterator<Entry<K, V>> aaa;

		public ValueIterator(Iterator<Entry<K, V>> aaa) {
			super();
			this.aaa = aaa;
		}

		@Override
		public boolean hasNext() {
			return aaa.hasNext();
		}

		@Override
		public V next() {
			return aaa.next().value;
		}

		@Override
		public void remove() {
			aaa.remove();
		}
	}

	private final class Values extends AbstractCollection<V> {
		@Override
		public Iterator<V> iterator() {
			return new ValueIterator(list.iterator());
		}

		@Override
		public int size() {
			return ArrayListMap.this.size();
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public void clear() {
			ArrayListMap.this.clear();
		}
	}

	private class KeyIterator implements Iterator<K> {
		Iterator<Entry<K, V>> aaa;

		public KeyIterator(Iterator<Entry<K, V>> aaa) {
			super();
			this.aaa = aaa;
		}

		@Override
		public boolean hasNext() {
			return aaa.hasNext();
		}

		@Override
		public K next() {
			return aaa.next().key;
		}

		@Override
		public void remove() {
			aaa.remove();
		}
	}

	private final class Keys extends AbstractSet<K> {

		@Override
		public Iterator<K> iterator() {
			return new KeyIterator(list.iterator());
		}

		@Override
		public int size() {
			return ArrayListMap.this.size();
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public void clear() {
			ArrayListMap.this.clear();
		}
	}

	public K keyAt(int index) {
		return entryAt(index).key;
	}

	public V valueAt(int index) {
		return entryAt(index).value;
	}

	public Entry<K, V> entryAt(int index) {
		return list.get(index);
	}
}
