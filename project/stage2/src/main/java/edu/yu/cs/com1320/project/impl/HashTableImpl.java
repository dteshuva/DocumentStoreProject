package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;
public class HashTableImpl<Key,Value> implements HashTable<Key,Value>{
	private class Entry<Key,Value>{
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
    private int n;
	public HashTableImpl(){
		this.table=new Entry[5];
        this.n=0;
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
		if(first.key.equals(k)){
			return (Value)first.value;
		}
		while(first.next!=null){
			Entry next=first.next;
			if(next.key.equals(k)){
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
        if(v==null){
            return delete(k);
        }
		if(this.table[index]==null){
			this.table[index]=new Entry(k,v);
            this.n++;
			checkSize();
			return null;
		}
		Entry first=this.table[index];
		if(first.key.equals(k)){
            return careValue(first, k,v); }
		while(first.next!=null){
            Entry next=first.next;
            if(next.key.equals(k)){
                return careValue(next, k, v);  } 
            first=next;
		}
		first.next=new Entry(k,v);
        this.n++;
		checkSize();
		return null;
	}
	private void checkSize(){
		if(this.n>=0.75*this.table.length){
			resize(); }
	}
    private Value careValue(Entry first,Key k,Value v){
			Value current=(Value)first.value;
			first.value=v;
			checkSize();
			return current;
    }
    private Value delete(Key k){
        int index=hash(k);
        if(this.table[index]==null){
            return null;
        }
        Entry first=this.table[index].next;
        Entry prev=this.table[index];
        if(prev.key.equals(k)){
            Value val=(Value)prev.value;
            n--;
            this.table[index]=first;
            return val;
        }
        while(first!=null){
            if(first.key.equals(k)){
                Value val=(Value)first.value;
                n--;
                prev.next=first.next;
                return val;
            }
            prev=prev.next;
            first=first.next;
        }
        return null;
    }
	private void resize() {
		int l= this.table.length*2;
		Entry[] oldTable = this.table;
		this.table = new Entry[l];

        this.n=0;
		for(int i=0; i<oldTable.length;i++){
            Entry element=oldTable[i];
			while(element!=null){
                put((Key)element.key,(Value)element.value);
                element=element.next;
			}
		}
	}
}