package com.jf;


import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class IndexTest extends LuceneBase{
	   
	    private Analyzer analyzer = standardAnalyzer;  
	    
	    /** 
	     * 创建索引 
	     * @throws IOException 
	     */  
	    @Test  
	    public void createIndex() throws IOException {  
	        //创建索引目录  
	        Directory directory = FSDirectory.open(indexFile);  
	        //建立索引创建类  
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer);  
	        indexWriterConfig.setOpenMode(OpenMode.CREATE);  //总是重新创建索引
	        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);  
	  
	        //建立索引  
	        File[] files = new File(dataDirectory).listFiles();  
	        if (files.length > 0) {  
	            for (int i = 0; i < files.length; i++) {  
	                Document document = new Document();
	                //document.add(new Field("content", new FileReader(files[i])));  
	                document.add(new Field("content", getContent(files[i]), Field.Store.YES, Field.Index.ANALYZED));  
	                document.add(new Field("title", files[i].getName(), Field.Store.YES, Field.Index.ANALYZED));  
	                writer.addDocument(document);  
	            }  
	            System.out.println("创建了" + writer.numDocs() + "索引");  
	        }  
	        writer.close();  
	    }  
	  
	    /** 
	     * 搜索文档 
	     * @throws IOException 
	     * @throws ParseException 
	     */  
	    @Test  
	    public void search() throws IOException, ParseException {  
	  
	        IndexReader indexReader = IndexReader.open(FSDirectory.open(indexFile));  
	        //创建搜索类  
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
	        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer);  
	        Query query = queryParser.parse("中国人");  
	        TopDocs topDocs = indexSearcher.search(query, 10000);  
	        System.out.println("一共查到:" + topDocs.totalHits + "记录");  
	        ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
	  
	        for (int i = 0; i < scoreDoc.length; i++) {  
	            //内部编号  
	            int docId = scoreDoc[i].doc;  
	            System.out.println("doc:" + docId);  
	            //根据文档id找到文档  
	            Document doc = indexSearcher.doc(docId); 
	            System.out.println("content:" + doc.get("content"));  
	        }  
	    }  

	}  
