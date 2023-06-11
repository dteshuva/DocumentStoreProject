package edu.yu.cs.com1320.project.stage5.impl;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import edu.yu.cs.com1320.project.stage5.Document;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class DocumentImpl implements Document {
	private URI uri;
	private String text;
	private byte[] binaryData;
	private Map<String,Integer> wordCounter;
    private transient long time;
	public DocumentImpl(URI uri, String txt, Map<String,Integer> wordMap){
		if(txt==null||uri==null||txt.length()==0){
			throw new IllegalArgumentException();
		}
        this.time=System.nanoTime();
		this.uri=uri;
		this.text=txt;
		this.binaryData=null;
		String t=text.toLowerCase();
		if(wordMap==null){
		this.wordCounter=new HashMap<>();
		String[]words=t.replaceAll("[^a-zA-Z0-9\\s]","").split(" ");
		for(int i=0; i<words.length; i++){
			if(wordCounter.containsKey(words[i])){
				wordCounter.put(words[i], wordCounter.get(words[i])+1);
			}
			else{
				wordCounter.put(words[i],1);
			}
		}
	  }
	  else{
		  this.wordCounter=wordMap;
	  }
	}
	public DocumentImpl(URI uri, String txt){
		if(txt==null||uri==null||txt.length()==0){
			throw new IllegalArgumentException();
		}
        this.time=System.nanoTime();
		this.uri=uri;
		this.text=txt;
		this.binaryData=null;
		String t=text.toLowerCase();
		this.wordCounter=new HashMap<>();
		String[]words=t.replaceAll("[^a-zA-Z0-9\\s]","").split(" ");
		for(int i=0; i<words.length; i++){
			if(wordCounter.containsKey(words[i])){
				wordCounter.put(words[i], wordCounter.get(words[i])+1);
			}
			else{
				wordCounter.put(words[i],1);
			}
		}
	}
	public DocumentImpl(URI uri,byte[]binaryData){
		if(uri==null||binaryData==null||binaryData.length==0){
			throw new IllegalArgumentException();
		}
		this.uri=uri;
		this.binaryData=binaryData;
		this.text=null;
        this.time=System.nanoTime();
		this.wordCounter=new HashMap<>();
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
    public int wordCount(String word){
		if(!wordCounter.containsKey(word.toLowerCase())){
			return 0;
		}
		return wordCounter.get(word.toLowerCase());
    }
    /**
     * @return all the words that appear in the document
     */
    public Set<String> getWords(){
		if(this.text==null){
			return new HashSet<>();
		}
		Set<String> allWords=this.wordCounter.keySet();
		return allWords;
    }
    public int hashCode(){
    	int result=uri.hashCode();
    	result=31*result+(text!=null ? text.hashCode() : 0);
    	result=31*result+Arrays.hashCode(binaryData);
    	return result;
    }
    public boolean equals(DocumentImpl other){
    	if(this.hashCode()==other.hashCode()){
			return true;
		}
		return false;
    }
   
    public long getLastUseTime(){
        return this.time;
    }
    public void setLastUseTime(long timeInNanoseconds){
        this.time=timeInNanoseconds;
    }
    @Override
    public int compareTo(Document o) {
		Long n1=(Long)this.time;
		Long n2=(Long)o.getLastUseTime();
        return n1.compareTo(n2);
    }
    @Override
    public Map<String, Integer> getWordMap() {
        Map<String,Integer> map=new HashMap<>();
		for(String st : this.getWords()){
			map.put(st, this.wordCounter.get(st));
		}
		return map;
    }
    @Override
    public void setWordMap(Map<String, Integer> wordMap) {
        this.wordCounter=wordMap;
    }   

    
}