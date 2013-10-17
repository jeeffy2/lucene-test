package com.jf;


import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTest extends LuceneBase{
	
	   private Analyzer analyzer = smartChineseAnalyzer;
	    /** 
	     * 创建索引 
	     * @throws IOException 
	     */  
	    @Test  
	    public void createIndex() throws IOException {  
	        //创建索引目录  
	        Directory directory = FSDirectory.open(indexFile);  
	        //建立索引创建类  
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_45, analyzer);  
	        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);  //总是重新创建索引
	        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);  
	  
	        //建立索引  
	        File[] files = new File(dataDirectory).listFiles();  
	        if (files.length > 0) {  
	            long time1 = System.currentTimeMillis();  
	            for (int i = 0; i < files.length; i++) {  
	                Document document = new Document();
	                //document.add(new Field("content", new FileReader(files[i])));  
	                FieldType fieldType = new FieldType();
	                fieldType.setIndexed(true);
	                fieldType.setStored(true);
	                document.add(new Field("content", getContent(files[i]), fieldType));  
	                document.add(new Field("title", files[i].getName(), fieldType));  
	                document.add(new LongField("size", files[i].length(), fieldType));  
	                writer.addDocument(document);  
	            }  
	            long time2 = System.currentTimeMillis();  
	            System.out.println("创建了" + writer.numDocs() + "索引");  
	            System.out.println("一共花了" + (time2 - time1) + "时间");  
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
	  
	        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexFile));  
	        //创建搜索类  
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
	        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_45, fields, analyzer);  
	        Query query = queryParser.parse("中国人");  
	        TopDocs topDocs = indexSearcher.search(query, 10000);  
	        System.out.println("一共查到:" + topDocs.totalHits + "记录");  
	        ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
	  
	        for (int i = 0; i < scoreDoc.length; i++) {  
	            //内部编号  
	            int doc = scoreDoc[i].doc;  
	            System.out.println("doc:" + doc);  
	            //根据文档id找到文档  
	            Document mydoc = indexSearcher.doc(doc); 
	            System.out.println("content:" + mydoc.get("content"));  
	        }  
	    }  
	    
	}  
