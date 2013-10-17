package com.jf;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class HighlighterTest extends LuceneBase{
	private File indexFile=new File("test/index");
	private Analyzer analyzer = new SmartChineseAnalyzer(Version.LUCENE_45,true);  
	
	@Test
    public void highlighter() throws Exception{
    	IndexReader indexReader = DirectoryReader.open(FSDirectory.open(indexFile));  
        //创建搜索类  
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
        
        //设置权重
        Map<String, Float> boosts = new HashMap<String, Float>();
		boosts.put("title", 10f);
		
//        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_45, fields, analyzer);  
        QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_45, fields, analyzer, boosts);  
        Query query = queryParser.parse("test");  
        TopDocs topDocs = indexSearcher.search(query, 10000);  
        System.out.println("一共查到:" + topDocs.totalHits + "记录");  
        ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
  
        // ============== 准备高亮器
     			Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
     			Scorer scorer = new QueryScorer(query);
     			Highlighter highlighter = new Highlighter(formatter, scorer);

     			Fragmenter fragmenter = new SimpleFragmenter(50);
     			highlighter.setTextFragmenter(fragmenter);
     	// ==============
     			
        for (int i = 0; i < scoreDoc.length; i++) {  
            //内部编号  
            int docSn = scoreDoc[i].doc;  
            System.out.println("doc:" + docSn);  
            //根据文档id找到文档  
            Document doc = indexSearcher.doc(docSn); 
//            System.out.println("content:" + doc.get("content"));  
            
            // =========== 高亮
			// 返回高亮后的结果，并进行摘要，如果当前属性值中没有出现关键字，会返回 null
			String hc = highlighter.getBestFragment(analyzer, "content", doc.get("content"));
			if (hc == null) {
				String content = doc.get("content");
				int endIndex = Math.min(50, content.length());
				hc = content.substring(0, endIndex);// 最多前50个字符
			}
			System.out.println("hc: " + hc);
			// ===========
        }  
    }
}
