package com.jf;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;


public class QueryTest extends LuceneBase{


	public void queryAndPrintResult(Query query){
		try {
			IndexReader indexReader = IndexReader.open(FSDirectory.open(indexFile)); 
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);  
        TopDocs topDocs = indexSearcher.search(query, 10000);  
        System.out.println("一共查到:" + topDocs.totalHits + "记录");  
        ScoreDoc[] scoreDoc = topDocs.scoreDocs;  
  
        for (int i = 0; i < scoreDoc.length; i++) {  
            //内部编号  
            int docId = scoreDoc[i].doc;  
            System.out.println("doc:" + docId);  
            //根据文档id找到文档  
            Document doc = indexSearcher.doc(docId); 
            System.out.println("title:" + doc.get("title"));  
            System.out.println("content:" + doc.get("content"));
            System.out.println("query: "+query);
        }  
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用查询语句查询
	 * 可以用query rewrite，实现信息过滤
	 */
	@Test
	public void testQueryString() {
		
		try {
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36, fields, standardAnalyzer);
//			Query query = queryParser.parse("content:test -title:test");
			Query query = queryParser.parse("test -file");
			queryAndPrintResult(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关键词查询
	 * title:test
	 */
	@Test
	public void testTermQuery() {
		
		Term term = new Term("title", "test");
		Query query = new TermQuery(term);

		queryAndPrintResult(query);
	}

	/**
	 * 通配符查询
	 * '?' 代表一个字符， '*' 代表0个或多个字符
	 * title:test*
	 * title:*t*
	 * title:tes?
	 */
	@Test
	public void testWildcardQuery() {
		//Term term = new Term("title", "tes?");
		// Term term = new Term("title", "te*"); // 前缀查询 PrefixQuery
		 Term term = new Term("title", "*t*");
		Query query = new WildcardQuery(term);

		queryAndPrintResult(query);
	}

	/**
	 * 条件查询
	 * 
	 */
	@Test
	public void testBooleanQuery() {
		// 条件1
		Term term1 = new Term("title", "*t*");
		Query query1 = new WildcardQuery(term1);

		// 条件2
		Term term = new Term("content", "file");
		Query query2 = new TermQuery(term);

		// 组合
		BooleanQuery boolQuery = new BooleanQuery();
		boolQuery.add(query1, Occur.MUST);
		boolQuery.add(query2, Occur.MUST);

		queryAndPrintResult(boolQuery);
	}

	

}
