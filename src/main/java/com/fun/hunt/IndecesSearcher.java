package com.fun.hunt;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;

public class IndecesSearcher {
	
	public static boolean isPresent(SearcherManager manager,QueryParser parser,String StrQuery) throws IOException, ParseException{
		Query query = parser.parse(parser.escape(StrQuery));
		IndexSearcher searcher = manager.acquire();
		TopDocs docs = searcher.search(query,1);
		if(docs!=null && docs.totalHits > 0){
			return true;
		}
		return false;
	}
}
