package com.jf;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LuceneBase {
	    protected String dataDirectory = "test/data";
	    protected File indexFile=new File("test/index");

	    protected Analyzer standardAnalyzer = new StandardAnalyzer(Version.LUCENE_45);
	    //中文分析器  
	    protected Analyzer smartChineseAnalyzer = new SmartChineseAnalyzer(Version.LUCENE_45,true);  
	    protected Analyzer smartIKAnalyzer = new IKAnalyzer(true);
	    protected Analyzer ikAnalyzer = new IKAnalyzer();
	    protected Analyzer cjkAnalyzer = new CJKAnalyzer(Version.LUCENE_45);
	    
	    
	    protected String[] fields={"title","content"};  
	   
	    protected String getContent(File file){
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
