import java.io.IOException;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.impl.DocumentStoreImpl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
public class DocumentTest{
	DocumentStoreImpl table=new DocumentStoreImpl();
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
		DocumentImpl doc1=new DocumentImpl(new URI("http://example.com/path"),"content for the test");
		DocumentImpl doc2=new DocumentImpl(new URI("http://example2.com/path"),arr);
		DocumentImpl doc3=new DocumentImpl(new URI("http://example.com/path"),"content for the test");
		DocumentImpl doc4=new DocumentImpl(new URI("http://example2.com/path"),arr);
		assertEquals(null,doc1.getDocumentBinaryData());
		assertEquals(null,doc2.getDocumentTxt());
		assertEquals("content for the test",doc1.getDocumentTxt());
		byte[]data=doc2.getDocumentBinaryData();
		boolean check=true;
		for(int i=0; i<data.length;i++){
			if(data[i]!=arr[i]){
				check=false;
			}
		}
		assertEquals(true,check);
		assertEquals(false,doc1.equals(doc2));
		assertEquals(true,doc1.equals(doc3));
		assertEquals(true,doc2.equals(doc4));
	}
}