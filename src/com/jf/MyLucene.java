package com.jf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
//import org.wltea.analyzer.lucene.IKAnalyzer;
//import org.wltea.analyzer.lucene.IKQueryParser;

public class MyLucene {

	private static final File INDEX_PATH = new File("D:\\lucene-test\\index");		// 索引文件位置, 当前路径下的index文件
	private static final  String filePath = "D:\\lucene-test\\data\\test.txt";// 索引数据源文件位置，当前路径下的luceneDataSource\test.txt文件
	//private static final Analyzer ANALYZER = new IKAnalyzer(); 		// 中文分词器
	private static final Analyzer ANALYZER = new SmartChineseAnalyzer(Version.LUCENE_36,true); 		// 中文分词器

	
	public MyLucene(){
	}
	
	/**
	 * 创建索引
	 */
	public void CreateIndex() {
		File readFile = new File(filePath);									// 获取数据源文件
		HashMap<String, String> words = readFile(readFile);

		Document doc = null;
		if (words != null) {
				try {
						IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_36, ANALYZER);
						writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
						IndexWriter writer = new IndexWriter(FSDirectory.open(INDEX_PATH), writerConfig);
		
						Set<String> keys = words.keySet();
		
						for (Iterator<String> it = keys.iterator(); it.hasNext();) {
							String key = it.next();
							doc = new Document();
							Field index = new Field("index", key, Field.Store.YES,Field.Index.ANALYZED);
							Field contents = new Field("contents", words.get(key),Field.Store.YES, Field.Index.NO);
							doc.add(index);
							doc.add(contents);
							writer.addDocument(doc);
						}
						writer.close();	// 这里不关闭建立索引会失败
				} catch (Exception e) {
						e.printStackTrace();
				}
		} 
		else 
				System.out.println("文件读取错误");

	}
	
	/**
	 * 判断索引库是已否创建
	 */
	public boolean noIndex() {
			File[] indexs = INDEX_PATH.listFiles();
			if (indexs.length == 0) {
				return true;
			} else {
				return false;
			}
	}
	
	/**
	 * 读取文件
	 * @param file
	 */
	public HashMap<String, String> readFile(File file) {
		InputStream in = null;
		InputStreamReader inR = null;
		BufferedReader br = null;
		HashMap<String, String> wordsMap = new HashMap<String, String>();
		try {
				in = new FileInputStream(file);
				inR = new InputStreamReader(in, "utf-8");
				br = new BufferedReader(inR);
				String line;
				while ((line = br.readLine()) != null) {
					wordsMap.put(line.trim(), line.trim());
				}
				return wordsMap;

		} catch (Exception e) {
				e.printStackTrace();
				return null;
		} finally {
			try {
				if (in != null)
					in.close();
				if (inR != null)
					inR.close();
				if (br != null)
					br.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * 检索
	 * @param queryStr
	 * @param hitsPerPage
	 */
	public void search(String queryStr) {
		
		try {
			IndexReader reader = IndexReader.open(FSDirectory.open(INDEX_PATH));// 得到索引的目录
			IndexSearcher searcher = new IndexSearcher(reader);

			QueryParser queryParser = new QueryParser(Version.LUCENE_36,"index", ANALYZER);
			Query query = queryParser.parse(queryStr);
			TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			if(hits.length > 0){
				System.out.println("检索词："+queryStr+"\t共找到 "+hits.length+"条记录");
				for (int i = 0; i < hits.length; i++) {
					Document result = searcher.doc(hits[i].doc);
					System.out.println((i+1) +")" + "\n  index:" + result.get("index") + "\n  contents:" + result.get("contents"));
				}
			}else{
				System.out.println("未找到结果");
			}
		} catch (Exception e) {
			System.out.println("Exception");
		}
	}
	
	
		
		@Test
		public  void test() {
			MyLucene myLucene = new MyLucene();
			// 索引库是已否创建,如果没有则创建
			/*if(myLucene.noIndex()){ 		
				myLucene.CreateIndex();
			}*/
			myLucene.CreateIndex();
			myLucene.search("Lucene");
			
		}
}