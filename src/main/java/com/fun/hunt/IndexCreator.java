package com.fun.hunt;

import java.io.IOException;

import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;


public class IndexCreator {
	
	public static void write(String[] colVals,String [] colName,IndexWriter indexWriter) throws IOException {
		Document document = new Document();
		for (int i = 0; i < colName.length; i++) {
			document.add(new TextField(colName[i],colVals[i],Field.Store.YES));
		}
		indexWriter.addDocument(document);
	}
}
