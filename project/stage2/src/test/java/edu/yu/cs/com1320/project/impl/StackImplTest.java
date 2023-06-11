package edu.yu.cs.com1320.project.impl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.Stack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
public class StackImplTest {
    StackImpl stack=new StackImpl();
    @Test
    public void empty(){
        StackImpl help=new StackImpl();
        assertEquals(0,help.size());
        assertEquals(null,help.peek());
        assertEquals(null,help.pop());
    }
    @Test
    public void pushValues(){
        stack.push("example 1");
        assertEquals("example 1",stack.peek());
        stack.push("example 2");
        assertEquals("example 2",stack.peek());
        stack.push("example 3");
        assertEquals("example 3",stack.peek());
        stack.push("example 4");
        assertEquals("example 4",stack.peek());
        stack.push("example 5");
        assertEquals("example 5",stack.peek());
        assertEquals(5,stack.size());
        assertEquals("example 5",stack.pop());
        assertEquals("example 4",stack.pop());
        assertEquals("example 3",stack.pop());
        assertEquals(2,stack.size());
    }
}