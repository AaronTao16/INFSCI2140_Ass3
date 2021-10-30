package Search;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Classes.Path;
import Classes.Query;
import Classes.Stemmer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class ExtractQuery {
	private String queryPath;
	private String stopWordPath;

	private BufferedReader topicReader;
	private BufferedReader stopWordReader;

	private Set<String> stopWord;

	private String next;

	private Pattern p = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

	public ExtractQuery() throws IOException {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		queryPath = Path.TopicDir;
		stopWordPath = Path.StopwordDir;
		topicReader = new BufferedReader(new FileReader(this.queryPath));
		stopWordReader = new BufferedReader(new FileReader(this.stopWordPath));
		stopWord = new HashSet<>();
		stopWordReader.lines().forEach(line -> this.stopWord.add(line.trim()));
		
		next = topicReader.readLine();
	}
	
	public boolean hasNext() {
		return next != null;
	}
	
	public Query next() throws IOException {
		Query query = new Query();
		StringBuilder content = new StringBuilder();
		if((next).equals("<top>")){
			next = topicReader.readLine();

			while(!next.equals("</top>")){
				String[] str = next.split(" ");
				String tag = str[0];
				if(tag.equals("<num>")){
					query.SetTopicId(str[2]);
					next = topicReader.readLine();
				} else if(tag.equals("<desc>") || tag.equals("<narr>")){
					next = topicReader.readLine();
					while(!p.matcher(next).find()){
						// [UNCOMMENT IF SEARCHING BY LONGER QUERY] (title + description + narrative)
//						content.append(p.matcher(next).replaceAll("").trim()).append(" ");
						next = topicReader.readLine();
					}
				} else {
					content.append(p.matcher(next).replaceAll("").trim()).append(" ");
					next = topicReader.readLine();
				}
			}
		}

		query.SetQueryContent(preProcessQuery(content.toString()));

//		System.out.println(content);
		next = topicReader.readLine();
		next = topicReader.readLine();
//		System.out.println("next : " + next);
		return query;
	}

	// 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
	private String preProcessQuery(String content) throws IOException {
		Analyzer analyzer = new StandardAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(content));
		tokenStream = new PorterStemFilter(tokenStream);
		tokenStream.reset();

		StringBuilder res = new StringBuilder();

		while(tokenStream.incrementToken()) {
			res.append(tokenStream.getAttribute(CharTermAttribute.class).toString()).append(" ");
		}

		return res.toString();
	}
}
