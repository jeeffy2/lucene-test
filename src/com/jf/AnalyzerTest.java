package com.jf;

import java.io.Reader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Test;

public class AnalyzerTest extends LuceneBase{

	

    @Test
    public void testAnalyzer() throws Exception {
    	Analyzer analyzer = standardAnalyzer; 
    	String str ="我们是中国人";  
//    	String str ="this is test file for lucene";  
    	analyzeIndex(analyzer, str);
    }
    
    private void analyzeIndex(Analyzer analyzer, String str) throws Exception {  
    	
        Reader reader = new StringReader(str);  
        //Reader reader = new FileReader(dataDirectory+"/stopWords.txt");   
        TokenStream ts = analyzer.tokenStream("content", reader);  
        String s1 = "", s2 = "";  
        while (ts.incrementToken()) {  
            CharTermAttribute ta = ts.getAttribute(CharTermAttribute.class);  
            s2 = ta.toString() + "/";  
            s1 += s2;  
        }  
        System.out.println("analyze result: "+s1);
    }  
}
