import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MovieTagsJoin {

    public static class MovieMapper extends Mapper<LongWritable, Text, Text, Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (key.get() == 0 && line.contains("movieId")) return;

            String[] fields = line.split(",", 3);
            if (fields.length >= 2) {
                String movieId = fields[0].trim();
                String title = fields[1].trim();
                context.write(new Text(movieId), new Text("MOVIE::" + title));
            }
        }
    }

    public static class TagMapper extends Mapper<LongWritable, Text, Text, Text> {

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            if (key.get() == 0 && line.contains("userId")) return;

            String[] fields = line.split(",", 4);
            if (fields.length >= 3) {
                String movieId = fields[1].trim();
                String tag = fields[2].trim();
                context.write(new Text(movieId), new Text("TAG::" + tag));
            }
        }
    }

    public static class JoinReducer extends Reducer<Text, Text, Text, Text> {

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String movieTitle = null;
            List<String> tags = new ArrayList<>();
            for (Text val : values) {
                String value = val.toString();
                if (value.startsWith("MOVIE::"))
                    movieTitle = value.substring(7);
                else if (value.startsWith("TAG::"))
                    tags.add(value.substring(5));
            }
            if (movieTitle != null && !tags.isEmpty()) {
                context.write(new Text(movieTitle), new Text("," + tags));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Movie Tags Join");
        job.setJarByClass(MovieTagsJoin.class);

        MultipleInputs.addInputPath(job, new Path(args[0]), TextInputFormat.class, MovieMapper.class);
        MultipleInputs.addInputPath(job, new Path(args[1]), TextInputFormat.class, TagMapper.class);

        job.setReducerClass(JoinReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, new Path(args[2]));
        job.waitForCompletion(true);
    }
}

/*
MovieTagsJoin Execution Commands
1. Navigate to your working directory
cd ~/mat

2. Compile the Java program
javac -classpath $(hadoop classpath) -d . MovieTagsJoin.java

3. Create a JAR file
jar cf MovieTagsJoin.jar *.class

4. Create input directories in HDFS
hadoop fs -mkdir -p /movies
hadoop fs -mkdir -p /tags

5. Copy input files to HDFS
hadoop fs -put -f movie.txt /movies/
hadoop fs -put -f tags.txt /tags/

6. Remove previous output directory (if exists)
hadoop fs -rm -r /movies_tags_out

7. Run the Hadoop job
hadoop jar MovieTagsJoin.jar MovieTagsJoin /movies /tags /movies_tags_out

8. Verify the output directory
hadoop fs -ls /movies_tags_out

9. View the results
hadoop fs -cat /movies_tags_out/part-r-00000 | head

*/
