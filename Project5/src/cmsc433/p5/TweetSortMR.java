package cmsc433.p5;

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * Map reduce which sorts the output of {@link TweetPopularityMR}.
 * The input will either be in the form of: </br>
 * 
 * <code></br>
 * &nbsp;(screen_name,  score)</br>
 * &nbsp;(hashtag, score)</br>
 * &nbsp;(tweet_id, score)</br></br>
 * </code>
 * 
 * The output will be in the same form, but with results sorted on the score.
 * 
 */
public class TweetSortMR {

	/**
	 * Minimum <code>int</code> value for a pair to be included in the output.
	 * Pairs with an <code>int</code> less than this value are omitted.
	 */
	private static int CUTOFF = 10;

	public static class SwapMapper
	extends Mapper<LongWritable, Text, IntWritable, Text > {

		String      id;
		int         score;
		SwapReducer sr = new SwapReducer();
		
		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String[] columns = value.toString().split("\t");
			id = columns[0];
			score = Integer.valueOf(columns[1]);
			
			// TODO: Your code goes here
			if (score >= CUTOFF) {
				context.write(new IntWritable(score), new Text(id));
			}
		}
	}

	public static class SwapReducer
	extends Reducer<IntWritable, Text, Text, IntWritable> {
		public Map<Text, IntWritable> map = new HashMap<Text , IntWritable>();
		public ArrayList<Text> textList = new ArrayList<Text>();
		public ArrayList<IntWritable> intList = new ArrayList<IntWritable>();

		@Override
		public void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			
			// TODO: Your code goes here		
			for (Text value: values) {
				context.write(new Text(value), key);
			}
		}
	}

	/**
	 * This method performs value-based sorting on the given input by configuring
	 * the job as appropriate and using Hadoop.
	 * 
	 * @param job
	 *          Job created for this function
	 * @param input
	 *          String representing location of input directory
	 * @param output
	 *          String representing location of output directory
	 * @return True if successful, false otherwise
	 * @throws Exception
	 */
	public static boolean sort(Job job, String input, String output, int cutoff)
			throws Exception {

		CUTOFF = cutoff;

		job.setJarByClass(TweetSortMR.class);

		// TODO: Set up map-reduce...
		job.setJobName("TweetSortMR");
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setMapperClass(SwapMapper.class);
		job.setReducerClass(SwapReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setWorkingDirectory(new Path(input));
		
		job.setSortComparatorClass(TweetComparator.class);

		// End

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		return job.waitForCompletion(true);
	}

}
