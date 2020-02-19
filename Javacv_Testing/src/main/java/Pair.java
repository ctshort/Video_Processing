//Cameron Short
//Implementing Map.Entry to record
//	fileName and dScore in a useful way

import java.util.Map;
import java.lang.UnsupportedOperationException;

public class Pair<K, V> implements Map.Entry<K, V>
{
	//Ctor
	public Pair(K key, V val) 
	{
		theKey = key;
		theVal = val;
	}
	
	@Override
	public K getKey()
	{
		return theKey;
	}
	@Override 
	public V getValue()
	{
		return theVal; 
	}
	@Override 
	public V setValue(V value)
	{
		throw new UnsupportedOperationException();
	}

	private K theKey; 
	private V theVal;
}
