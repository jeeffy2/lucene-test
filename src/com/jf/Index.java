/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Index {
	private static String indexPath = "/lucene-test";// Ë÷Òý´æ·ÅÄ¿Â¼

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_34, analyzer);

			// if (create) {
			// // Create a new index in the directory, removing any
			// // previously indexed documents:
			// iwc.setOpenMode(OpenMode.CREATE);
			// } else {
			// // Add new documents to an existing index:
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			// }

			// Optional: for better indexing performance, if you
			// are indexing many documents, increase the RAM
			// buffer. But if you do this, increase the max heap
			// size to the JVM (eg add -Xmx512m or -Xmx1g):
			//
			// iwc.setRAMBufferSizeMB(256.0);

			IndexWriter writer = new IndexWriter(dir, iwc);
			writer.deleteAll();
			Scanner scanner = new Scanner(new FileInputStream("/lucene-test/d.txt"), "UTF-8");
			try {
				int ii = 0;
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					String[] elems = line.split(",");
					System.out.println(elems[0]);
					if (elems.length > 2) {
						ii += 1;

						Document doc = new Document();
						Field f0 = new Field("id", Integer.toString(ii), Field.Store.YES, Field.Index.NOT_ANALYZED);
						Field f1 = new Field("title", elems[0], Field.Store.YES, Field.Index.ANALYZED);
						Field f2 = new Field("link", elems[1], Field.Store.YES, Field.Index.NO);

						System.out.println(elems[2]);
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
						Date date = new Date();
						String dateString = "2007-07-07 20:29";
						dateString = elems[2];
						try {
							date = df.parse(dateString);
							System.out.println(date.toLocaleString());
						} catch (Exception ex) {
							System.out.println(ex.getMessage());
						}

						NumericField f3 = new NumericField("date", Field.Store.YES, true);
						f3.setLongValue(date.getTime());
						doc.add(f3);
						System.out.println(elems[2]);

						doc.add(new NumericField("read", Field.Store.YES, true).setIntValue(Integer.parseInt(elems[3])));
						doc.add(new NumericField("comment", Field.Store.YES, true).setIntValue(Integer.parseInt(elems[4])));

						doc.add(f0);
						doc.add(f1);
						doc.add(f2);

						writer.addDocument(doc);
					}

				}
			} finally {
				scanner.close();
			}
			Document doc = new Document();
			Field f = new Field("title", "test", Field.Store.YES, Field.Index.ANALYZED);

			doc.add(f);
			writer.addDocument(doc);
			// NOTE: if you want to maximize search performance,
			// you can optionally call optimize here. This can be
			// a costly operation, so generally it¡¯s only worth
			// it when your index is relatively static (ie you¡¯re
			// done adding documents to it):
			//
			writer.forceMerge(1);
			// Term term=new Term("link","http://www.cnblogs.com/lexus/archive/2011/09/30/2196819.html");
			// writer.deleteDocuments(term);
			Term term = new Term("id", "2162");
			writer.deleteDocuments(term);
			writer.close();

			System.out.println(" caught b ");
			System.out.println(new Date());
			System.out.println(new Date().getTime());

		} catch (IOException e) {
			System.out.println(" caught a ");
		}

	}

}
