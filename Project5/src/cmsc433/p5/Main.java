package cmsc433.p5;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.GenericOptionsParser;

public class Main {
	/**
	 * Temporary directory used to store output from TweetPopularityMR to feed as
	 * input into TweetSortMR
	 */
	protected static final String TEMP_DIR = "temp";

	/**
	 * Entry-point for the program. Should accept three command line arguments:
	 * <ul>
	 * <li><code>param </code>The parameter on which to map reduce the data set.
	 * The choices are 'user', 'tweet', 'hashtag', or 'hashtag_pair'. For example, if 'tweet' is
	 * specified, then the map reduce will rank the tweets.</li>
	 * <li><code>cutoff </code>The min score required for an entry in order for it
	 * to be listed in the results of the map reduce.</li>
	 * <li><code>in </code>Path to directory containing posts to match</li>
	 * <li><code>out </code>Path to output directory to store final result</li>
	 * </ul>
	 * The final output should be a list of post titles followed by their
	 * relevance count sorted in descending order.
	 */
	public static void main(String[] args) throws Exception {
		// Create configuration and parse arguments
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args)
		.getRemainingArgs();
		if (otherArgs.length != 4) {
			System.err
			.println("Usage: Main <user/tweet/hashtag> <cutoff> <in> <out>");
			System.exit(2);
		}
		// "otherArgs" contains {<user/tweet/hashtag>, <cutoff> <in>, <out>}

		int cutoff = 0;
		TrendingParameter param = null;

		switch (otherArgs[0].toLowerCase()) {
		case "user":
			param = TrendingParameter.USER;
			break;
		case "tweet":
			param = TrendingParameter.TWEET;
			break;
		case "hashtag":
			param = TrendingParameter.HASHTAG;
			break;
		case "hashtag_pair":
			param = TrendingParameter.HASHTAG_PAIR;
			break;
		default:
			System.err.println("Invalid paramater for MapReduce: " + otherArgs[0]
					+ ". Must be 'user', 'tweet', or 'hashtag'.");
			System.exit(2);
		}

		try {
			cutoff = Integer.valueOf(otherArgs[1]);
		} catch (NumberFormatException e) {
			System.err.println("Invalid paramater for MapReduce: " + otherArgs[1]
					+ ". Must be an integer.");
			System.exit(2);
		}

		boolean success = TweetPopularityMR.score(new Job(conf, "popularity"),
				otherArgs[2], TEMP_DIR, param);

		if (success)
			success = TweetSortMR.sort(new Job(conf, "sort"), TEMP_DIR, otherArgs[3],
					cutoff);

		System.exit(success ? 0 : 1);
	}

}
