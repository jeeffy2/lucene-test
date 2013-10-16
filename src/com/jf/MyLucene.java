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

	private static final File INDEX_PATH = new File("D:\\lucene-test\\index");		// �����ļ�λ��, ��ǰ·���µ�index�ļ�
	private static final  String filePath = "D:\\lucene-test\\data\\test.txt";// ��������Դ�ļ�λ�ã���ǰ·���µ�luceneDataSource\test.txt�ļ�
	//private static final Analyzer ANALYZER = new IKAnalyzer(); 		// ���ķִ���
	private static final Analyzer ANALYZER = new SmartChineseAnalyzer(Version.LUCENE_36,true); 		// ���ķִ���

	
	public MyLucene(){
	}
	
	/**
	 * ��������
	 */
	public void CreateIndex() {
		File readFile = new File(filePath);									// ��ȡ����Դ�ļ�
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
						writer.close();	// ���ﲻ�رս���������ʧ��
				} catch (Exception e) {
						e.printStackTrace();
				}
		} 
		else 
				System.out.println("�ļ���ȡ����");

	}
	
	/**
	 * �ж����������ѷ񴴽�
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
	 * ��ȡ�ļ�
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
	 * ����
	 * @param queryStr
	 * @param hitsPerPage
	 */
	public void search(String queryStr) {
		
		try {
			IndexReader reader = IndexReader.open(FSDirectory.open(INDEX_PATH));// �õ�������Ŀ¼
			IndexSearcher searcher = new IndexSearcher(reader);

			QueryParser queryParser = new QueryParser(Version.LUCENE_36,"index", ANALYZER);
			Query query = queryParser.parse(queryStr);
			TopScoreDocCollector collector = TopScoreDocCollector.create(100, true);
			searcher.search(query, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			if(hits.length > 0){
				System.out.println("�����ʣ�"+queryStr+"\t���ҵ� "+hits.length+"����¼");
				for (int i = 0; i < hits.length; i++) {
					Document result = searcher.doc(hits[i].doc);
					System.out.println((i+1) +")" + "\n  index:" + result.get("index") + "\n  contents:" + result.get("contents"));
				}
			}else{
				System.out.println("δ�ҵ����");
			}
		} catch (Exception e) {
			System.out.println("Exception");
		}
	}
	
	
		
		@Test
		public  void test() {
			MyLucene myLucene = new MyLucene();
			// ���������ѷ񴴽�,���û���򴴽�
			/*if(myLucene.noIndex()){ 		
				myLucene.CreateIndex();
			}*/
			myLucene.CreateIndex();
			myLucene.search("Lucene");
			
		}
}