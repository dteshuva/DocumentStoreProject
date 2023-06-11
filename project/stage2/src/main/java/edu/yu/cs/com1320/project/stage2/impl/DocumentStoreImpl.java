package edu.yu.cs.com1320.project.stage2.impl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.Document;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import edu.yu.cs.com1320.project.stage2.impl.DocumentImpl;
import edu.yu.cs.com1320.project.Command;
public class DocumentStoreImpl implements DocumentStore{
	private HashTableImpl table;
    private StackImpl command;
	public DocumentStoreImpl(){
		this.table=new HashTableImpl();
        this.command=new StackImpl();
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
		DocumentImpl document=makeDoc(uri,input, format);
		DocumentImpl doc=(DocumentImpl)getDocument(uri);
		if(this.table.put(uri,(Document)document)==null){
			Function<URI,Boolean> undo= Uri -> { this.table.put(Uri,null);
				return true; };
			this.command.push(new Command(uri,undo));
			return 0;
		}
		Function<URI,Boolean> undo=Uri -> { this.table.put(Uri,(Document)doc);
		return true; } ;
		this.command.push(new Command(uri,undo));
		return doc.hashCode();
    	}
	private DocumentImpl makeDoc(URI uri,InputStream input,DocumentFormat format)throws IOException{
		byte[]arr=input.readAllBytes();
		DocumentImpl document;
		if(format==DocumentStore.DocumentFormat.TXT){
			String str=new String(arr);
			document=new DocumentImpl(uri,str);
		}
		    else{
		        document=new DocumentImpl(uri,arr);
		}
		return document;
	}
	public Document getDocument(URI uri){
		return (Document) this.table.get(uri);
	}
	public boolean deleteDocument(URI uri){
		Document doc=(Document)this.table.put(uri,null);
		if(doc==null){
			return false;
		}
		Function<URI,Boolean> undo=Uri -> {this.table.put(Uri,doc);
			return true;	};
		this.command.push(new Command(uri,undo));
		return true;
	}
    public void undo() throws IllegalStateException{
        if(this.command.size()==0){
            throw new IllegalStateException();
        }
		Command prev=(Command)this.command.pop();
        prev.undo();
    }
    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException{
        if(this.command.size()==0){
            throw new IllegalStateException();
        }
        boolean check=false;
		StackImpl help=new StackImpl();
		while(this.command.size()!=0){
			Command head=(Command)this.command.peek();
			if(head.getUri().equals(uri)){
				Command prev=(Command)this.command.pop();
                prev.undo();
				check=true;
				break;
			}
			help.push(this.command.pop());
		}
		while(help.size()!=0){
			this.command.push(help.pop());
		}
		if(!check){
			throw new IllegalStateException();
		}
    }
}