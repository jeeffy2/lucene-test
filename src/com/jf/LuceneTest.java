package com.jf;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneTest {
	    private String dataSourceFile = "test/data";
	    private File indexFile=new File("test/index");
	    //���������ķ�����  
	    private Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_36,true);  
	    private Analyzer analyzer2 = new StandardAnalyzer(Version.LUCENE_36);
	    private Analyzer analyzer3 = new IKAnalyzer(true);
	    
	    private String[] fields={"title","content"};  
	   
	    /** 
	     * �������� 
	     * @throws IOException 
	     */  
	    @Test  
	    public void createIndex() throws IOException {  
	        //��������Ŀ¼  
	        Directory directory = FSDirectory.open(indexFile);  
	        //��������������  
	        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_36, analyzer2);  
	        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);  //�������´�������
	        IndexWriter writer = new IndexWriter(directory, indexWriterConfig);  
	  
	        //��������  
	        File[] files = new File(dataSourceFile).listFiles();  
	        if (files.length > 0) {  
	            long time1 = System.currentTimeMillis();  
	            for (int i = 0; i < files.length; i++) {  
	                Document document = new Document();
	                //document.add(new Field("content", new FileReader(files[i])));  
	                document.add(new Field("content", getContent(files[i]), Field.Store.YES, Field.Index.ANALYZED));  
	                document.add(new Field("title", files[i].getName(), Field.Store.YES, Field.Index.ANALYZED));  
	                writer.addDocument(document);  
	            }  
	            long time2 = System.currentTimeMillis();  
	            System.out.println("������" + writer.numDocs() + "����");  
	            System.out.println("һ������" + (time2 - time1) + "ʱ��");  
	        }  
	  
	        writer.close();  
	    }  
	  
	    /** 
	     * �����ĵ� 
	     * @throws IOException 
	     * @throws ParseException 
	     */  
	    @Test  
	    public void search() throws IOException, ParseException {  
	  
	        IndexReader indexReader = IndexReader.open(FSDirectory.open(indexFile));  
	        //����������  
	        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
	        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer);  
	        Query query = queryParser.parse("�й���");  
	        TopDocs topDocs = indexSearcher.search(query, 10000);  
	        System.out.println("һ���鵽:" + topDocs.totalHits + "��¼");  
	        ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
	  
	        for (int i = 0; i < scoreDoc.length; i++) {  
	            //�ڲ����  
	            int doc = scoreDoc[i].doc;  
	            System.out.println("doc:" + doc);  
	            //�����ĵ�id�ҵ��ĵ�  
	            Document mydoc = indexSearcher.doc(doc); 
	            System.out.println("content:" + mydoc.get("content"));  
	        }  
	    }  

	    @Test  
	    public void analyzerIndex() throws Exception {  
	    	Analyzer a = analyzer3; 
	    	String s ="�������й���";  
//	    	String s ="this is test file for lucene";  
	        StringReader reader = new StringReader(s);  
	        TokenStream ts = a.tokenStream("", reader);  
	        String s1 = "", s2 = "";  
	        //boolean hasnext= ts.incrementToken();  
	        //Token t = ts.next();  
	        while (ts.incrementToken()) {  
	            CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);  
	              
	            s2 = ta.toString() + "/";  
	            s1 += s2;  
	        }  
	        System.out.println("analyze result: "+s1);
	    }  
	    
	    private String getContent(File file){
	    	StringBuffer sb = new StringBuffer();
	    	try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
				String str = null;
				while((str = br.readLine())!=null){
					sb.append(str+"\n");
				}
				br.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return sb.toString();
	    }
	}  
