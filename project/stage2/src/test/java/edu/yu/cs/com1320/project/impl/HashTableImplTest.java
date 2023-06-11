package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
public class HashTableImplTest{
	HashTableImpl table=new HashTableImpl();
	@BeforeEach
	public void insertValues(){
		int count=1;
		for(Character i='A';i<='Z';i++){
			if(i!='R'){
				this.table.put(i,count);
				count++;
			}
		}
	}
	@Test
	public void checkValues(){
		int count=1;
		for(Character i='A';i<='Z';i++){
			if(i!='R'){
				assertEquals(count,this.table.get(i),"Value was supposed to be different");
				count++;
			}
			else{
				assertEquals(null,this.table.get(i),"Value of R is supposed to be null");
			}
		}
		this.table.put('Q',null);
		this.table.put('C',100);
		assertEquals(null,this.table.get('Q'),"Value of Q was supposed to be null");
		assertEquals(100,this.table.get('C'),"Value of C was supposed to be 100");
		assertEquals(100,table.put('C',200),"put(c) was supposed to return 100");
		assertEquals(200,this.table.get('C'),"Value of C was supposed to be 200");
		assertEquals(null,this.table.put('R',null),"Put(R,null) was supposed to return null");
		assertEquals(null,table.put('R',150),"Put(R,150) was supposed to return null");
		assertEquals(150,table.get('R'),"Value of R was supposed to be 150 but returned");
	}
	@Test
	public void manyValues(){
		HashTableImpl chart=new HashTableImpl();
		for(int i=1;i<=10000;i++){
			chart.put(i,i);
		}
		for(int i=1;i<=10000;i++){
			assertEquals(i,chart.get(i),"expected value was "+i+", not "+chart.get(i));
		}
	}
}