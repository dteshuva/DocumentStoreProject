package edu.yu.cs.com1320.project.stage5.impl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class TrieImplTest2{
    private TrieImpl<Integer> trie;
    @Test
    public void getAndPut(){
        trie=new TrieImpl<>();
        for(int i=1; i<=6;i++){
        trie.put("all", i);}
        for(int i=7;i<=12;i++){
            trie.put("booya",i);
        }
        Comparator<Integer> comp=(first,second)->first-second;
        comp=comp.reversed();
        List<Integer> ls1=trie.getAllSorted("all",comp );
        int count=6;
        assertEquals(6,ls1.size());
        for(Integer num : ls1){
            assertEquals(num,count);
            count--;
        }
        count=12;
        assertEquals(6,trie.getAllSorted("booya",comp ).size());
        for(Integer num : trie.getAllSorted("booya",comp )){
            assertEquals(num,count);
            count--;
        }
    }
    @Test 
    public void deleteAndGetWithPrefix(){
        trie=new TrieImpl<>();
        trie.put("too",1);
        trie.put("than",2);
        trie.put("TOOBL",3);
        trie.put("banf",4);
        trie.put("Toodle",5);
        trie.put("a",6);
        trie.put("Tootie",7);
        trie.put("shlotz",8);
        trie.put("tOoadama",9);
        trie.put("baduk",10);
        Comparator<Integer> comp=(first,second)->first-second;
        comp=comp.reversed();
        List<Integer> allPrefix=trie.getAllWithPrefixSorted("Too", comp);
        assertEquals(5,allPrefix.size());
        assertEquals(6,trie.getAllWithPrefixSorted("t", comp).size());
        int count=9;
        for(Integer num : allPrefix){
            assertEquals(num,count);
            assertFalse(allPrefix.contains(count+1));
            count=count-2;
        }
        Set<Integer> del=trie.deleteAllWithPrefix("Too");
        assertEquals(5,del.size());
        for(int i=1;i<=10;i+=2){
            assertTrue(del.contains(i));
            assertFalse(del.contains(i+1));
        }
        assertEquals(0,trie.getAllWithPrefixSorted("bash", comp).size());
        assertEquals(0,trie.getAllWithPrefixSorted("Too", comp).size());
        assertTrue(trie.deleteAllWithPrefix("Too").isEmpty());
        assertEquals(2,trie.getAllWithPrefixSorted("ba", comp).size());
        assertEquals(1,trie.getAllWithPrefixSorted("t", comp).size());
    }
    @Test
    public void testDelete(){
        trie=new TrieImpl<>();
        for(int i=1; i<=6;i++){
        trie.put("all", i);}
        for(int i=7;i<=12;i++){
            trie.put("booya",i);
        }
        Comparator<Integer> comp=(first,second)->first-second;
        comp=comp.reversed();
        assertEquals(6,trie.getAllWithPrefixSorted("all", comp).size());
        assertEquals(2,trie.delete("all",2));
        assertEquals(null,trie.delete("all",7));
        assertEquals(7,trie.delete("booya",7));
        assertEquals(5,trie.getAllWithPrefixSorted("all", comp).size());
        assertEquals(5,trie.getAllSorted("all", comp).size());
        assertFalse(trie.getAllWithPrefixSorted("all", comp).contains(2));
        assertFalse(trie.getAllWithPrefixSorted("booya", comp).contains(7));
    }
    @Test 
    public void testDeleteAll(){
        trie=new TrieImpl<>();
        Comparator<Integer> comp=(first,second)->first-second;
        comp=comp.reversed();
        for(int i=1; i<=6;i++){
        trie.put("all", i);}
        for(int i=1;i<=6;i++){
            trie.put("booya",i);
        }
        int count=6;
        assertEquals(6,trie.getAllSorted("booya",comp ).size());
        for(Integer num : trie.getAllSorted("booya",comp )){
            assertEquals(num,count);
            count--;
        }
        Set<Integer> del=trie.deleteAll("booya");
        assertEquals(6,del.size());
        assertTrue(trie.getAllWithPrefixSorted("booya", comp).isEmpty());
        count=6;
        assertEquals(6,trie.getAllSorted("all",comp ).size());
        for(Integer num : trie.getAllSorted("all",comp )){
            assertEquals(num,count);
            count--;
        }
        

    }

}