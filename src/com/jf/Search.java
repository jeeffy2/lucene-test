/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package com.jf;

import java.io.File;
import java.io.IOException;
import java.sql.Date;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Search {
private static String indexPath = "/lucene-test";//索引存放目录
/**
* @param args
* @throws IOException
* @throws CorruptIndexException
* @throws ParseException
*/
public static void main(String[] args) throws CorruptIndexException, IOException, ParseException {
// TODO Auto-generated method stub
IndexSearcher searcher = new IndexSearcher(FSDirectory.open(new File(indexPath)));
System.out.println("total blogs:"+searcher.getIndexReader().numDocs());
Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
QueryParser parser = new QueryParser(Version.LUCENE_36, "title", analyzer);//有变化的地方
Query query = parser.parse("test");
query = parser.parse("lucene");

//        SortField s1=new SortField("read",SortField.INT,true);
List<SortField> sortFields = new ArrayList<SortField>();
sortFields.add(new SortField("read", SortField.INT, true));
sortFields.add(new SortField("date", SortField.LONG, true));
SortField[] aa=new SortField[2];
sortFields.toArray(aa);
Sort sort=new Sort(aa);

//        Sort sort=new Sort();
//        sort.setSort(s1);

TopFieldDocs tfd=searcher.search(query,100,sort);
ScoreDoc[] hits = tfd.scoreDocs;
//        TopScoreDocCollector collector = TopScoreDocCollector.create(100,false);//有变化的地方
//        searcher.search(query, collector);
//        ScoreDoc[] hits = collector.topDocs().scoreDocs;

System.out.println(hits.length);
for (int i = 0; i < hits.length; i++) {
Document doc = searcher.doc(hits[i].doc);//new method is.doc()
System.out.print(doc.getFieldable("id")+" "+doc.getFieldable("title")+"   "+hits[i].toString()+" ");
System.out.print("=="+hits[i].doc+"====");
System.out.print(doc.getFieldable("link"));
Format formatter;
formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
String s = formatter.format(Long.parseLong(doc.get("date")));

System.out.print(s+" ");
System.out.println(Integer.parseInt(doc.get("read")));
}

//        System.out.println("Found " + collector.getTotalHits());
System.out.println("Found "+tfd.totalHits);
}

 

}