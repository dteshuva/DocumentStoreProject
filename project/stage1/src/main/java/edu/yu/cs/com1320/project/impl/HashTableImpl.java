package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
public class HashTableImpl<Key,Value> implements HashTable<Key,Value>{
	class Entry<Key,Value>{
		Key key;
		Value value;
		Entry next; 
		Entry(Key k,Value v){
			if(k==null){
				throw new IllegalArgumentException();
			}
			key=k;
			value=v;
			Entry next=null;
		}
	}
	private Entry[] table;
	public HashTableImpl(){
		this.table=new Entry[5];
	}
	private int hash(Key key){
		return (key.hashCode() & 0x7fffffff) % this.table.length;
	}
	public Value get(Key k){
		if(k==null){
			throw new IllegalArgumentException("Key is null");
		}
		int index=hash(k);
		if(this.table[index]==null){
			return null;
		}
		Entry first=this.table[index];
		if(first.key==k){
			return (Value)first.value;
		}
		while(first.next!=null){
			Entry next=first.next;
			if(next.key==k){
				return (Value)next.value;
			}
			first=next;
		}
		return null;
	}
	/**
     * @param k the key at which to store the value
     * @param v the value to store.
     * To delete an entry, put a null value.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
	public Value put(Key k, Value v){
		int index=hash(k);
		if(this.table[index]==null){
			this.table[index]=new Entry(k,v);
			return null;
		}
		Entry first=this.table[index];
		if(first.key==k){
			Value current=(Value)first.value;
			first.value=v;
			return current;
		}
		while(first.next!=null){
            Entry next=first.next;
            if(next.key==k){
            	Value current=(Value)next.value;
            	next.value=v;
            	return current;
            } 
            first=next;
		}
		first.next=new Entry(k,v);
		return null;
	}
}