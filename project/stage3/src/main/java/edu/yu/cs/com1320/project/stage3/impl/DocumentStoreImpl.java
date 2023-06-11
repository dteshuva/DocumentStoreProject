package edu.yu.cs.com1320.project.stage3.impl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import edu.yu.cs.com1320.project.Trie;
import edu.yu.cs.com1320.project.impl.TrieImpl;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.Undoable;
public class DocumentStoreImpl implements DocumentStore{
	private HashTableImpl table;
    private StackImpl<Undoable>command;
    private Trie wordTrack;
	public DocumentStoreImpl(){
		this.table=new HashTableImpl();
        this.command=new StackImpl();
        this.wordTrack=new TrieImpl();
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
        updateTrack(doc, document);
		if(this.table.put(uri,(Document)document)==null){
			Function<URI,Boolean> undo= Uri -> { this.table.put(Uri,null);
                deleteWords(document);
				return true; };
			this.command.push(new GenericCommand<URI>(uri,undo));
			return 0;
		}
		Function<URI,Boolean> undo=Uri -> { this.table.put(Uri,(Document)doc);
            updateTrack(document, doc);
		return true; } ;
		this.command.push(new GenericCommand<URI>(uri,undo));
		return doc.hashCode();
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
	public Document getDocument(URI uri){
		return (Document) this.table.get(uri);
	}
	public boolean deleteDocument(URI uri){ //have to deal with commands and undo
		Document doc=(Document)this.table.put(uri,null);
		if(doc==null){
			return false;
		}
		Function<URI,Boolean> undo=Uri -> {this.table.put(Uri,doc);
            updateTrack(null,(DocumentImpl)doc);
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
					break;
				}
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
	private boolean undoSet(boolean check,URI uri){ //not done
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
		return this.wordTrack.getAllSorted(keyword,comp.reversed());
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
        return this.wordTrack.getAllWithPrefixSorted(keywordPrefix.toLowerCase(),comp.reversed());
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
			wordsSet.addAll(d.getWords());
			deletedURI.add(d.getKey());
			this.table.put(d.getKey(),null);
		}
		removeTrace(wordsSet, deleted, c);
		this.command.push(c);
		return deletedURI;
	}
}