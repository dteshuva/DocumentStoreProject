package edu.yu.cs.com1320.project.stage4.impl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
public class DocumentStoreImpl implements DocumentStore{
	private HashTable table;
    private StackImpl<Undoable>command;
    private Trie wordTrack;
	private int maxCount;
	private int maxByte;
	private int docCount;
	private int byteCount;
	private MinHeap<Document> priority;

	public DocumentStoreImpl(){
		this.table=new HashTableImpl();
        this.command=new StackImpl();
		this.priority=new MinHeapImpl<>();
        this.wordTrack=new TrieImpl();
		this.docCount=0;
		this.byteCount=0;
		this.maxByte=Integer.MAX_VALUE;
		this.maxCount=Integer.MAX_VALUE;
	}
	/**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     * @throws IOException if there is an issue reading input
     * @throws IllegalArgumentException if uri or format are null
     */
	public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{ //have to deal with commands and undo
		if(format==null || uri==null){
			throw new IllegalArgumentException("uri or format is null"); }
		if(input==null){
			return deleteDoc(uri, input); }
		DocumentImpl document=makeDoc(uri,input, format);
		DocumentImpl doc=(DocumentImpl)getDoc(uri);
        updateTrack(doc, document);
		if(this.table.put(uri,(Document)document)==null){
			updateCounters(document);
			Function<URI,Boolean> undo= Uri -> { this.table.put(Uri,null);
                deleteWords(document);
				undoCounters(document);
				return true; };
			this.command.push(new GenericCommand<URI>(uri,undo));
			if(this.byteCount>this.maxByte||this.docCount>this.maxCount){
				overMax(); }
			return 0;
		}
		replaceCounters(doc, document);
		Function<URI,Boolean> undo=Uri -> { this.table.put(Uri,(Document)doc);
			doc.setLastUseTime(System.nanoTime());
			replaceCounters(document, doc);
            updateTrack(document, doc);
		return true; } ;
		this.command.push(new GenericCommand<URI>(uri,undo));
		if(this.byteCount>this.maxByte||this.docCount>this.maxCount){
			overMax(); }
		return doc.hashCode();
    	}
	private void replaceCounters(DocumentImpl doc,DocumentImpl document){
		undoCounters(doc);
		updateCounters(document);
		outOfHeap(doc);
		this.priority.insert(document);
	}
		//when either counter exceeds its maximum value, deleting documents from the heap, trie and table until there is enough space
	private void overMax(){
		while(this.byteCount>this.maxByte||this.docCount>this.maxCount){
			Document doc=this.priority.remove();
			this.priority.insert(doc);
            uriInCommand(doc.getKey());
		}
	}
	private void uriInCommand(URI uri){
		Stack<Undoable>help=new StackImpl<>();
		while(this.command.size()!=0){
			Undoable prev=this.command.pop();
			if(prev instanceof GenericCommand){
				GenericCommand gen=(GenericCommand)prev;
				if(gen.getTarget().equals(uri)){
					gen.undo();}
				else{ 
					help.push(prev); }
			}
			else{
				CommandSet allCommands=(CommandSet)prev;
				if(allCommands.containsTarget(uri)){
					allCommands.undo(uri); }
				if(allCommands.size()!=0){
					help.push(prev); }
			}
		}
		while(help.size()!=0){
			this.command.push(help.pop());
		}
	}
	private int deleteDoc(URI uri,InputStream input){
		DocumentImpl doc=(DocumentImpl)getDocument(uri);
			if(!deleteDocument(uri)){
				return 0;
			}
			return doc.hashCode();
	}
	private void undoCounters(DocumentImpl document){
		outOfHeap((Document)document);
		this.docCount--;
		if(document.getDocumentTxt()!=null){
			this.byteCount=this.byteCount-document.getDocumentTxt().getBytes().length;
		}
		else{
			this.byteCount=this.byteCount-document.getDocumentBinaryData().length;
		}
	}
	private void updateCounters(DocumentImpl document){
		this.priority.insert((Document)document);
		this.docCount++;
		if(document.getDocumentTxt()!=null){
			this.byteCount=this.byteCount+document.getDocumentTxt().getBytes().length;
		}
		else{
			this.byteCount=this.byteCount+document.getDocumentBinaryData().length;

		}
	}
        //makes a new document from the parameters received in putDocument
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
	//deletes from the trie the words of the old document and add the words of the new document
    private void updateTrack(DocumentImpl old,DocumentImpl newDoc){
        Set<String> newWords=newDoc.getWords();
        for(String word : newWords){
            this.wordTrack.put(word,newDoc);
        }
        if(old!=null){
            Set<String> oldWords=old.getWords();
            for(String word: oldWords){
                this.wordTrack.delete(word,old);
            }
        }
    }
	private Document getDoc(URI uri){
		Document doc=(Document) this.table.get(uri);
		return doc;
	}
	public Document getDocument(URI uri){
		Document doc=(Document) this.table.get(uri);
		if(doc==null){
			return null;
		}
		doc.setLastUseTime(System.nanoTime());
        this.priority.reHeapify(doc);
		return doc;
	}
	//deleting a document out of the heap
	private void outOfHeap(Document doc){
		boolean f=false;
		int count=0;
		Stack<Document>help=new StackImpl<>();
		while(!f&&count<this.docCount){
			Document d=this.priority.remove();
			count++;
			if(d.equals(doc)){
				f=true;
			}
			else{
				help.push(d);
			}
		}
		while(help.size()!=0){
			this.priority.insert(help.pop());
		}
	}
	public boolean deleteDocument(URI uri){ 
		Document doc=(Document)this.table.put(uri,null);
		if(doc==null){
			return false;
		}
		undoCounters( (DocumentImpl)doc);
		Function<URI,Boolean> undo=Uri -> {this.table.put(Uri,doc);
            updateTrack(null,(DocumentImpl)doc);
			doc.setLastUseTime(System.nanoTime());
			updateCounters( (DocumentImpl)doc);
			return true;	};
        deleteWords((DocumentImpl)doc);
		this.command.push(new GenericCommand<URI>(uri,undo));
		return true;
	}
	//delete all the words in a document from the Trie
    private void deleteWords(DocumentImpl doc){
        Set<String> words=doc.getWords();
        for(String word : words){
            this.wordTrack.delete(word,doc);
        }
    }
    public void undo() throws IllegalStateException{ //update undo
        if(this.command.size()==0){
            throw new IllegalStateException();
        }
		Undoable prev=this.command.pop();
		prev.undo();
		if(this.docCount>this.maxCount||this.byteCount>this.byteCount){
			overMax();}
    }
    /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    public void undo(URI uri) throws IllegalStateException{ //update undo, in the middle
        if(this.command.size()==0){
            throw new IllegalStateException();
        }
        boolean check=false;
		StackImpl<Undoable>help=new StackImpl<>();
		while(this.command.size()!=0){
			Undoable head=this.command.peek();
			if(head instanceof GenericCommand){
				if(undoSingle(check,uri)){
					check=true;
					break;
				}
			}
			else{
				if(undoSet(check, uri)){
					check=true;
					break;	}
			}
			help.push(this.command.pop());
		}
		while(help.size()!=0){
			this.command.push(help.pop());
		}
		if(this.docCount>this.maxCount||this.byteCount>this.byteCount){
			overMax();}
		if(!check){
			throw new IllegalStateException(); 	}
    }
	private boolean undoSet(boolean check,URI uri){ //find specifiv generic command
		CommandSet c=(CommandSet)this.command.peek();
		if(c.containsTarget(uri)){
			c.undo(uri);
			check=true;
			if(c.size()==0){
			this.command.pop();
			}
			return true;
		}
		return false;
	}
	private boolean undoSingle(boolean check,URI uri ){
		GenericCommand c=(GenericCommand)this.command.peek();
		if(c.getTarget().equals(uri)){
			Undoable prev=this.command.pop();
			prev.undo();
			check=true;
			return true;
		}
		return false;
	}
	/**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword){
		Comparator<Document> comp=(first,second)->first.wordCount(keyword)-second.wordCount(keyword);
		List<Document> lst=this.wordTrack.getAllSorted(keyword,comp.reversed());
		for(Document d : lst){
			d.setLastUseTime(System.nanoTime());
			this.priority.reHeapify(d);
		}
		return lst;
	}
	/**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix){ 
		Comparator<Document> comp=(first,second)->startWith(first, keywordPrefix.toLowerCase())-startWith(second, keywordPrefix.toLowerCase());
		List<Document> lst=this.wordTrack.getAllWithPrefixSorted(keywordPrefix.toLowerCase(),comp.reversed());
        for(Document d : lst){
			d.setLastUseTime(System.nanoTime());
			this.priority.reHeapify(d);
		}
		return lst;
	}
	//returns the amount of words in a document that start with the prefix
	private int startWith(Document d1,String prefix){
		int count=0;
		for(String st : d1.getDocumentTxt().toLowerCase().split(" ")){
			if(st.startsWith(prefix)){
				count++;
			}
		}
		return count;
	}
	/**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAll(String keyword){
		Set<Document> deleted=this.wordTrack.deleteAll(keyword.toLowerCase());
		Set<URI> deletedURI=new HashSet<>();
		Set<String> wordSet=new HashSet<>();
		CommandSet c=new CommandSet();
		for(Document d : deleted){
			wordSet.addAll(d.getWords());
			deletedURI.add(d.getKey());
			this.table.put(d.getKey(),null);
			undoCounters((DocumentImpl)d);
		}
		wordSet.remove(keyword);
		removeTrace(wordSet, deleted, c);
		this.command.push(c);
		return deletedURI;
	}
	//removing from the trieimpl all the words in each document
	private void removeTrace(Set<String> words,Collection<Document> documents,CommandSet c){
		for(Document d : documents){
			for(String w : words){
				if(d.getWords().contains(w)){
					this.wordTrack.delete(w,d);
				}
			}
			Function<URI,Boolean> undo=Uri -> {this.table.put(Uri,d);
				updateTrack(null, (DocumentImpl)d);
				updateCounters( (DocumentImpl)d);
				d.setLastUseTime(System.nanoTime());
				return true; };
			c.addCommand(new GenericCommand<URI>(d.getKey(),undo));
		}
	}
	/**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
		Comparator<Document> comp=(first,second)->startWith(first, keywordPrefix.toLowerCase())-startWith(second, keywordPrefix.toLowerCase());
		List<Document> deleted=this.wordTrack.getAllWithPrefixSorted(keywordPrefix.toLowerCase(),comp.reversed());
		Set<URI> deletedURI=new HashSet<>();
		Set<String> wordsSet=new HashSet<>();
		CommandSet c= new CommandSet<>();
		for(Document d : deleted){
			undoCounters( (DocumentImpl)d);
			wordsSet.addAll(d.getWords());
			deletedURI.add(d.getKey());
			this.table.put(d.getKey(),null);
		}
		removeTrace(wordsSet, deleted, c);
		this.command.push(c);
		return deletedURI;
	}
	
	public void setMaxDocumentCount(int limit) {
		this.maxCount=limit;
		if(this.docCount>this.maxCount||this.byteCount>this.byteCount){
			overMax();}
	}
	@Override
	public void setMaxDocumentBytes(int limit) {
		this.maxByte=limit;
		if(this.docCount>this.maxCount||this.byteCount>this.byteCount){
			overMax();}
	}
}