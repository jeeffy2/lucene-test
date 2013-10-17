package com.jf;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class FilterTest extends LuceneBase{

	@Test  
    public void testFilter() throws IOException, ParseException {  
  
        IndexReader indexReader = IndexReader.open(FSDirectory.open(indexFile));  
        //创建搜索类  
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, fields, standardAnalyzer);  
        Query query = queryParser.parse("test");  
        
        //TermsFilter filter = new TermsFilter();
        //Term term = new Term("content", "-file");
        //filter.addTerm(term);

        //Filter filter=new QueryWrapperFilter(new WildcardQuery(new Term("content", "file")));
        Filter filter=new MyFilter();
        
        TopDocs topDocs = indexSearcher.search(query, filter, 100);  
        System.out.println("一共查到:" + topDocs.totalHits + "记录");  
        ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
  
        for (int i = 0; i < scoreDoc.length; i++) {  
            //内部编号  
            int docId = scoreDoc[i].doc;  
            System.out.println("doc:" + docId);  
            //根据文档id找到文档  
            Document doc = indexSearcher.doc(docId); 
            System.out.println("title:" + doc.get("title"));  
        }  
    }  
}
