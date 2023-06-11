package edu.yu.cs.com1320.project.stage4.impl;
import java.io.IOException;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Set;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class DocumentTest{
	DocumentStoreImpl table=new DocumentStoreImpl();
	@Test
	public void testUndo() throws URISyntaxException, IOException{
		DocumentStoreImpl table2=new DocumentStoreImpl();
		try{
			table2.undo();
			fail("URI doesn't exist. Should've thrown an illegalStateException");
		} catch(IllegalStateException e){}
		URI[]arr=new URI[6];
		String initialString = "text1";
		arr[0]=new URI("http://example1.com/path");arr[1]=new URI("http://example2.com/path");arr[2]=new URI("http://example3.com/path");arr[3]=new URI("http://example4.com/path");arr[4]=new URI("http://example5.com/path");arr[5]=new URI("http://example6.com/path");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[0],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text2";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[1],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text3";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
         assertEquals(0,table2.putDocument(targetStream,arr[2],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
        initialString = "text4";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[3],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		initialString = "text5";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[4],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		initialString = "text6";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[5],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		assertEquals("text6",table2.getDocument(arr[5]).getDocumentTxt());
		table2.undo();
		assertEquals(null,table2.getDocument(arr[5]));
		assertEquals("text3",table2.getDocument(arr[2]).getDocumentTxt());
		table2.undo(arr[2]);
		assertEquals(null,table2.getDocument(arr[2]));
		table2.undo(arr[0]);
		assertEquals(null,table2.getDocument(arr[0]));
		try{
			table2.undo(arr[2]);
			fail("URI doesn't exist. Should've thrown an illegalStateException");
		} catch(IllegalStateException e){}
		try{
			table2.undo(arr[0]);
			fail("URI doesn't exist. Should've thrown an illegalStateException");
		} catch(IllegalStateException e){}
		try{
			table2.undo(new URI("http://example1.com/path"));
			fail("URI doesn't exist. Should've thrown an illegalStateException");
		} catch(IllegalStateException e){}
	}
	@Test
	public void testUndoExist()throws URISyntaxException, IOException{
		DocumentStoreImpl table2=new DocumentStoreImpl();
		URI[]arr=new URI[6];
		String initialString = "text1";
		arr[0]=new URI("http://example1.com/path");arr[1]=new URI("http://example2.com/path");arr[2]=new URI("http://example3.com/path");arr[3]=new URI("http://example4.com/path");arr[4]=new URI("http://example5.com/path");arr[5]=new URI("http://example6.com/path");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[0],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
        initialString = "text2";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[1],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text3";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
         assertEquals(0,table2.putDocument(targetStream,arr[2],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
         assertEquals("text3",table2.getDocument(arr[2]).getDocumentTxt());       
		 initialString = "text4";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[3],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		initialString = "text5";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[4],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		initialString = "text6";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[5],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		assertEquals("text6",table2.getDocument(arr[5]).getDocumentTxt());
		initialString="test1";
		targetStream = new ByteArrayInputStream(initialString.getBytes());
        table2.putDocument(targetStream,arr[0],DocumentStore.DocumentFormat.TXT);
		initialString="test6";
		targetStream = new ByteArrayInputStream(initialString.getBytes());
        table2.putDocument(targetStream,arr[5],DocumentStore.DocumentFormat.TXT);
		initialString="test3";
		targetStream = new ByteArrayInputStream(initialString.getBytes());
        table2.putDocument(targetStream,arr[2],DocumentStore.DocumentFormat.TXT);
		assertEquals("test3",table2.getDocument(arr[2]).getDocumentTxt());
		assertEquals("test1",table2.getDocument(arr[0]).getDocumentTxt());
		assertEquals("test6",table2.getDocument(arr[5]).getDocumentTxt());
		table2.undo(arr[2]);
		table2.undo(arr[0]);
		table2.undo(arr[5]);
		assertEquals("text3",table2.getDocument(arr[2]).getDocumentTxt());
		assertEquals("text1",table2.getDocument(arr[0]).getDocumentTxt());
		assertEquals("text6",table2.getDocument(arr[5]).getDocumentTxt());
		table2.undo(arr[5]);
		table2.undo(arr[0]);
		table2.undo(arr[2]);
		assertEquals(null,table2.getDocument(arr[2]));
		assertEquals(null,table2.getDocument(arr[0]));
		assertEquals(null,table2.getDocument(arr[5]));
	}
	@Test
	public void testDelete() throws URISyntaxException, IOException{
		DocumentStoreImpl table2=new DocumentStoreImpl();
		URI[]arr=new URI[6];
		String initialString = "text1";
		arr[0]=new URI("http://example1.com/path");arr[1]=new URI("http://example2.com/path");arr[2]=new URI("http://example3.com/path");arr[3]=new URI("http://example4.com/path");arr[4]=new URI("http://example5.com/path");arr[5]=new URI("http://example6.com/path");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[0],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text2";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[1],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text3";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
         assertEquals(0,table2.putDocument(targetStream,arr[2],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
        initialString = "text4";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[3],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		initialString = "text5";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[4],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		initialString = "text6";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table2.putDocument(targetStream,arr[5],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
		assertEquals("text6",table2.getDocument(arr[5]).getDocumentTxt());
		assertEquals(true,table2.deleteDocument(arr[0]));
		assertEquals(true,table2.deleteDocument(arr[2]));
		assertEquals(true,table2.deleteDocument(arr[5]));
		assertEquals(null,table2.getDocument(arr[0]));
		assertEquals(null,table2.getDocument(arr[2]));
		assertEquals(null,table2.getDocument(arr[5]));
		table2.undo(arr[5]);
		table2.undo(arr[0]);
		table2.undo(arr[2]);
		assertNotEquals(null,table2.getDocument(arr[0]));
		assertNotEquals(null,table2.getDocument(arr[5]));
		assertNotEquals(null,table2.getDocument(arr[2]));
		table2.undo(arr[5]);
		table2.undo(arr[0]);
		table2.undo(arr[2]);
		assertEquals(null,table2.getDocument(arr[0]));
		assertEquals(null,table2.getDocument(arr[2]));
		assertEquals(null,table2.getDocument(arr[5]));

		

	}
	@Test
	public void testDocTable() throws URISyntaxException, IOException{
		URI[]arr=new URI[5];
		String initialString = "text1";
		arr[0]=new URI("http://example1.com/path");arr[1]=new URI("http://example2.com/path");arr[2]=new URI("http://example3.com/path");arr[3]=new URI("http://example4.com/path");arr[4]=new URI("http://example5.com/path");
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table.putDocument(targetStream,arr[0],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text2";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
         assertEquals(0,table.putDocument(targetStream,arr[1],DocumentStore.DocumentFormat.BINARY),"value supposed to be 0");
        initialString = "text3";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
         assertEquals(0,table.putDocument(targetStream,arr[2],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
        initialString = "text4";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertEquals(0,table.putDocument(targetStream,arr[3],DocumentStore.DocumentFormat.TXT),"value supposed to be 0");
        assertEquals("text3",table.getDocument(arr[2]).getDocumentTxt());
        assertEquals("text4",table.getDocument(arr[3]).getDocumentTxt());
        assertEquals(null,table.getDocument(arr[4]));
        assertEquals(false,table.deleteDocument(arr[4]));
        assertEquals(true,table.deleteDocument(arr[1]));
		assertEquals(false,table.deleteDocument(arr[1]));
        assertEquals(null,table.getDocument(arr[1]));
		assertEquals(0,table.putDocument(null,arr[1],DocumentStore.DocumentFormat.BINARY));
        initialString = "testing";
        targetStream = new ByteArrayInputStream(initialString.getBytes());
        assertNotEquals(0,table.putDocument(targetStream,arr[2],DocumentStore.DocumentFormat.TXT));
        assertNotEquals(0,table.putDocument(null,arr[2],DocumentStore.DocumentFormat.TXT));
        assertEquals(null,table.getDocument(arr[2]));
	}
	@Test
	public void testDocument() throws URISyntaxException{
		byte[]arr=new byte[10];
		DocumentImpl doc1=new DocumentImpl(new URI("http://example.com/path"),"content for the -test");
		DocumentImpl doc2=new DocumentImpl(new URI("http://example2.com/path"),arr);
		DocumentImpl doc3=new DocumentImpl(new URI("http://example.com/path"),"content for the test5");
		DocumentImpl doc4=new DocumentImpl(new URI("http://example2.com/path"),arr);
		DocumentImpl doc5=new DocumentImpl(new URI("http://example.com/path"),"content con3 con. con con for@ for 598 test ok SHEMA");
		assertEquals(null,doc1.getDocumentBinaryData());
		assertEquals(null,doc2.getDocumentTxt());
		assertEquals("content for the -test",doc1.getDocumentTxt());
		byte[]data=doc2.getDocumentBinaryData();
		boolean check=true;
		for(int i=0; i<data.length;i++){
			if(data[i]!=arr[i]){
				check=false;
			}
		}
		assertEquals(true,check);
		Set<String> words= doc5.getWords();
		assertEquals(8,words.size());
		assertEquals(0,doc2.getWords().size());
		assertEquals(3,doc5.wordCount("con"));
		assertEquals(3,doc5.wordCount("Con"));
		assertEquals(1,doc5.wordCount("con3"));
		assertEquals(1,doc5.wordCount("contEnt"));
		assertEquals(1,doc5.wordCount("oK"));
		assertEquals(0,doc5.wordCount("nope"));
		assertEquals(2,doc5.wordCount("for"));
		assertEquals(2,doc5.wordCount("FOR"));
		assertEquals(false,doc1.equals(doc2));
		assertEquals(false,doc1.equals(doc3));
		assertEquals(true,doc2.equals(doc4));
	}
}