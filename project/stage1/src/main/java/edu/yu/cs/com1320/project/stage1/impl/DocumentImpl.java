package edu.yu.cs.com1320.project.stage1.impl;
import java.net.URI;
import java.util.Arrays;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;

public class DocumentImpl implements Document{
	private URI uri;
	private String text;
	private byte[] binaryData;
	public DocumentImpl(URI uri, String txt){
		if(txt==null||uri==null||txt.length()==0){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.text=txt;
		this.binaryData=null;
	}
	public DocumentImpl(URI uri,byte[]binaryData){
		if(uri==null||binaryData==null||binaryData.length==0){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.binaryData=binaryData;
		this.text=null;
	}
	public String getDocumentTxt(){
		return this.text;
	}
	public byte[] getDocumentBinaryData(){
		if(binaryData==null){
			return null;
		}
		byte[]copy=new byte[this.binaryData.length];
		for(int i=0; i<copy.length;i++){
			copy[i]=this.binaryData[i];
		}
		return copy;
	}
	public URI getKey(){
		return this.uri;
	}
    public int hashCode(){
    	int result=uri.hashCode();
    	result=31*result+(text!=null ? text.hashCode() : 0);
    	result=31*result+Arrays.hashCode(binaryData);
    	return result;
    }
    public boolean equals(DocumentImpl other){
    	return (this.hashCode()==other.hashCode());
    }
}