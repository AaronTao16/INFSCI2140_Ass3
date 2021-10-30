import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Classes.*;
import IndexingLucene.*;
import Search.*;

/**
 * !!! YOU CANNOT CHANGE ANYTHING IN THIS CLASS !!!
 * 
 * Main class for running your HW3.
 * 
 */
public class HW3Main {

	public static void main(String[] args) throws Exception {
		// Open index
		MyIndexReader ixreader = new MyIndexReader("trectext");
		Map<Double, List<List<Document>>> map = new HashMap<>();
		// [ADD] For change µ
//		for(double µ = 1000.0; µ <= 3000.0; µ += 500){
			// Initialize the MyRetrievalModel
			QueryRetrievalModel model = new QueryRetrievalModel(ixreader);
			// [ADD] For change µ
//			QueryRetrievalModel model = new QueryRetrievalModel(ixreader, µ);
			// Extract the queries
			ExtractQuery queries = new ExtractQuery();

			long startTime = System.currentTimeMillis();
			// [ADD] For change µ
//			List<List<Document>> forQuery = new ArrayList<>();
			while (queries.hasNext()) {

				Query aQuery = queries.next();
				System.out.println(aQuery.GetTopicId() + "\t" + aQuery.GetQueryContent());
				// conduct retrieval on the index for each topic, and return top 25 documents
				List<Document> results = model.retrieveQuery(aQuery, 20);
				if (results != null) {
					int rank = 1;
					for (Document result : results) {
						System.out.println(aQuery.GetTopicId() + " Q0 " + result.docno() + " " + rank + " " + result.score() + " MYRUN");
						rank++;
					}
				}
				// [ADD] For change µ
//				forQuery.add(results);
			}
			// [ADD] For change µ
//			map.put(µ, forQuery);


			long endTime = System.currentTimeMillis(); // end time of running code
			System.out.println("\n\n4 queries search time: " + (endTime - startTime) / 60000.0 + " min");
//		}
		ixreader.close();

		// [ADD] For change µ
//		for (int q = 0; q < 4; q++) {
//			for (int i = 0; i < 20; i++) {
//				for(Double key: map.keySet()){
//					System.out.print((i+1) + " " + map.get(key).get(q).get(i).docno() + " " + " " + map.get(key).get(q).get(i).score() + "	");
//				}
//				System.out.println();
//			}
//			System.out.println();
//		}
	}

}
