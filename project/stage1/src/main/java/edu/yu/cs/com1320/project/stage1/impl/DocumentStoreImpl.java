package edu.yu.cs.com1320.project.stage1.impl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage1.Document;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
public class DocumentStoreImpl implements DocumentStore{
	private HashTableImpl table;
	public DocumentStoreImpl(){
		this.table=new HashTableImpl();
	}
	/**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
	public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{
		if(format==null || uri==null){
			throw new IllegalArgumentException("uri or format is null");
		}
		if(input==null){
			DocumentImpl doc=(DocumentImpl)getDocument(uri);
			if(!deleteDocument(uri)){
				return 0;
			}
			return doc.hashCode();
		}
		byte[]arr=input.readAllBytes();
		DocumentImpl document;
		if(format==DocumentStore.DocumentFormat.TXT){
			String str=new String(arr);
			document=new DocumentImpl(uri,str);
		}
		    else{
		        document=new DocumentImpl(uri,arr);
		}
		DocumentImpl doc=(DocumentImpl)getDocument(uri);
		if(this.table.put(uri,(Document)document)==null){
			return 0;
		}
		return doc.hashCode();
	}
	public Document getDocument(URI uri){
		return (Document) this.table.get(uri);
	}
	public boolean deleteDocument(URI uri){
		if(this.table.put(uri,null)==null){
			return false;
		}
		return true;
	}
}