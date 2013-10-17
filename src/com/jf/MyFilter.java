package com.jf;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.OpenBitSet;

public class MyFilter extends Filter{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException  
    {  
        OpenBitSet result = new OpenBitSet(reader.maxDoc());  
//        TermDocs td = reader.termDocs();  
        TermDocs td = reader.termDocs(null);  

        while (td.next())  
        {  
            Document doc = reader.document(td.doc());  
            String field = doc.get("content");  
            if (!field.contains("file2"))  
            {  
                result.set((long) td.doc());  
            }  
        }  
        return result;  
    }  

}
