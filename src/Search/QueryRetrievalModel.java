package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	
	protected MyIndexReader indexReader;

	private double µ = 2000;
	
	public QueryRetrievalModel(MyIndexReader ixreader) {
		indexReader = ixreader;
	}

	// tune µ
	public QueryRetrievalModel(MyIndexReader ixreader, double µ) {
		indexReader = ixreader;
		this.µ = µ;
	}

	/**
	 * Search for the topic information. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */
	
	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low

		String[] tokens = String.valueOf(aQuery.GetQueryContent()).split("\\s+");
		// Max heap order by score
		PriorityQueue<Document> priorityQueue = new PriorityQueue<>(TopN, (o1, o2) -> Double.compare(o2.score(), o1.score()));
		Map<String, Double> P_wrMap = new HashMap<>(); // <token, P(w|D)>
		Map<Integer, Map<String, Integer>> C_wdMap = new HashMap<>(); // <docId, <token, freq>>

		for(String token: tokens){
			if(indexReader.CollectionFreq(token) == 0) continue;
			// Calculating P(w | R) in collections
			double p_wr = (double) indexReader.CollectionFreq(token)/indexReader.collectionLength();
			P_wrMap.put(token, p_wr);

			// Calculating term frequency in every documents
			if(indexReader.getPostingList(token) != null){
				for(int[] arr : indexReader.getPostingList(token)){
					if(arr == null) continue;
					Map<String, Integer> C_wd = C_wdMap.getOrDefault(arr[0], new HashMap<>());
					C_wd.put(token, arr[1]);
					C_wdMap.put(arr[0], C_wd);
				}
			}
		}

		for(int k: C_wdMap.keySet()){
			double score = 1;
			// |D| document length
			int D = indexReader.docLength(k);
			for (String token: P_wrMap.keySet()){
				// Dirichlet Prior Smoothing
				// score = (C(w, D) + µ * P(w|R)) / (|D| + µ)
				score *= (C_wdMap.get(k).getOrDefault(token, 0) + µ * P_wrMap.get(token)) / (D + µ);
			}
			Document doc = new Document(k+"", indexReader.getDocno(k), score);
			priorityQueue.add(doc);
		}

		List<Document> result = new ArrayList<>();
		int index = 1;
		while(!priorityQueue.isEmpty() && index++ <= 20){
			result.add(priorityQueue.poll());
		}

		return result;
	}
}