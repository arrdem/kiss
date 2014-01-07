package kiss.lang;

import clojure.lang.IMapEntry;

/**
 * A kiss environement mapping
 * 
 * 
 * @author Mike
 *
 */
public class Mapping {
	Object type;
	Object value;
	
	public Object getValue() {
		return value;
	}

	public IMapEntry toMapEntry(Object key) {
		return new Entry(key,value);
	}
	
	public static class Entry implements IMapEntry {
		private Object key;
		private Object value;

		public Entry(Object key, Object value) {
			this.key=key;
			this.value=value;
		}

		@Override
		public Object getKey() {
			return key;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public Object setValue(Object value) {
			throw new UnsupportedOperationException("Mapping entry is immutable!");
		}

		@Override
		public Object key() {
			return key;
		}

		@Override
		public Object val() {
			return value;
		}
		
	}
}