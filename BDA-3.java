import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.conf.Configuration;

public class MyMaxMin {

    public static class MaxTemperatureMapper extends Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString();

            if (!(line.length() == 0)) {
                String date = line.substring(6, 14);
                float temp_Max = Float.parseFloat(line.substring(39, 45).trim());
                float temp_Min = Float.parseFloat(line.substring(47, 53).trim());

                if (temp_Max > 35.0) {
                    context.write(new Text("Hot Day " + date), new Text(String.valueOf(temp_Max)));
                }

                if (temp_Min < 10) {
                    context.write(new Text("Cold Day " + date), new Text(String.valueOf(temp_Min)));
                }
            }
        }
    }

    public static class MaxTemperatureReducer extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            String temperature = values.iterator().next().toString();
            context.write(key, new Text(temperature));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "weather example");
        job.setJarByClass(MyMaxMin.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setMapperClass(MaxTemperatureMapper.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        Path outputPath = new Path(args[1]);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, outputPath);

        outputPath.getFileSystem(conf).delete(outputPath, true);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}




/*
README Commands for Running MyMaxMin Hadoop Job
# 1. Change to the directory containing your Java file and input
cd ~/Desktop/mat

# 2. Make sure the Java file name matches the public class
mv mymaxmin.java MyMaxMin.java

# 3. Compile the Java program using Hadoop classpath
javac -classpath $(hadoop classpath) -d . MyMaxMin.java

# 4. Create a JAR file from the compiled classes
jar cf MyMaxMin.jar *.class

# 5. Create an input directory in HDFS
hadoop fs -mkdir -p /weather

# 6. Copy the local input file to HDFS
hadoop fs -copyFromLocal dataofweatherfile.txt /weather/

# 7. (Optional) List files in HDFS to confirm
hadoop fs -ls /weather

# 8. (Optional) View the input file in HDFS
hadoop fs -cat /weather/dataofweatherfile.txt

# 9. Remove previous output directory if it exists
hadoop fs -rm -r /weather_out

# 10. Run the Hadoop job
hadoop jar MyMaxMin.jar MyMaxMin /weather /weather_out

# 11. List files in the output directory
hadoop fs -ls /weather_out

# 12. View the output results
hadoop fs -cat /weather_out/part-r-00000

*/
