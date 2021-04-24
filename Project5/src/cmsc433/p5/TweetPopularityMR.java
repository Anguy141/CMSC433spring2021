package cmsc433.p5;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * Map reduce which takes in a CSV file with tweets as input and output
 * key/value pairs.</br>
 * </br>
 * The key for the map reduce depends on the specified {@link TrendingParameter}
 * , <code>trendingOn</code> passed to
 * {@link #score(Job, String, String, TrendingParameter)}).
 */
public class TweetPopularityMR {

	// For your convenience...
	public static final int TWEET_SCORE = 1;
	public static final int RETWEET_SCORE = 3;
	public static final int MENTION_SCORE = 1;
	public static final int PAIR_SCORE = 1;
	public static final int HASHTAG_SCORE = 1;

	// Is either USER, TWEET, HASHTAG, or HASHTAG_PAIR. Set for you before call to
	// map()
	private static TrendingParameter trendingOn;

	public static class TweetMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			// Converts the CSV line into a tweet object
			Tweet tweet = Tweet.createTweet(value.toString());

			// TODO: Your code goes here
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (trendingOn == TrendingParameter.USER) {
				context.write(new Text(tweet.getUserScreenName()), new IntWritable(TWEET_SCORE));

				if (tweet.wasRetweetOfUser()) {
					context.write(new Text(tweet.getRetweetedUser()), new IntWritable(RETWEET_SCORE));
				}

				for (String user : tweet.getMentionedUsers()) {
					context.write(new Text(user), new IntWritable(MENTION_SCORE));
				}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			} else if (trendingOn == TrendingParameter.TWEET) {
				context.write(new Text(tweet.getId().toString()), new IntWritable(TWEET_SCORE));

				if (tweet.wasRetweetOfTweet()) {
					context.write(new Text(tweet.getRetweetedTweet().toString()), new IntWritable(RETWEET_SCORE));
				}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			} else if (trendingOn == TrendingParameter.HASHTAG) {
				for (String str : tweet.getHashtags()) {
					context.write(new Text(str.replace("#", "")), new IntWritable(HASHTAG_SCORE));
				}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////	
			} else if (trendingOn == TrendingParameter.HASHTAG_PAIR) { // unique pairs = n(n-1)/2
				int tweetHashtagsSize = tweet.getHashtags().size();
				
				if (tweetHashtagsSize >= 2) {
					for (int i = 0; i < tweetHashtagsSize - 1; i++) {
						for (int j = i + 1; j < tweetHashtagsSize; j++) {
							String Hashtag1 = tweet.getHashtags().get(i).replace("#", "");
							String Hashtag2 = tweet.getHashtags().get(j).replace("#", "");
							
							if (Hashtag1.compareTo(Hashtag2) > 0) { // reverse alphabetical order
								context.write(new Text("(" + Hashtag1 + "," + Hashtag2 + ")"),
										new IntWritable(HASHTAG_SCORE));
							} else {
								context.write(new Text("(" + Hashtag2 + "," + Hashtag1 + ")"),
										new IntWritable(HASHTAG_SCORE));
							}
						}
					}
				}
			////////////////////////////////////////////////////////////////////////////////////////////////////////////
			}
		}// end of map method
	}

	public static class PopularityReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {

			// TODO: Your code goes here
			int score = 0;
			for (IntWritable value : values) {
				score += value.get();
			}

			context.write(new Text(key.toString()), new IntWritable(score));
		}
	}

	/**
	 * Method which performs a map reduce on a specified input CSV file and outputs
	 * the scored tweets, users, or hashtags.</br>
	 * </br>
	 * 
	 * @param job
	 * @param input      The CSV file containing tweets
	 * @param output     The output file with the scores
	 * @param trendingOn The parameter on which to score
	 * @return true if the map reduce was successful, false otherwise.
	 * @throws Exception
	 */
	public static boolean score(Job job, String input, String output, TrendingParameter trendingOn) throws Exception {

		TweetPopularityMR.trendingOn = trendingOn;

		job.setJarByClass(TweetPopularityMR.class);

		// TODO: Set up map-reduce...
		job.setJobName("TweetPopularityMR");
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setMapperClass(TweetMapper.class);
		job.setReducerClass(PopularityReducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		// End

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job.waitForCompletion(true);
	}

}