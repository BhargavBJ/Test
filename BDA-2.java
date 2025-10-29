import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class MatMul {

    public static class MatrixMapper extends Mapper<LongWritable, Text, Text, Text> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            Configuration conf = context.getConfiguration();
            int m = Integer.parseInt(conf.get("m"));
            int p = Integer.parseInt(conf.get("p"));
            String line = value.toString();
            String[] indicesAndValue = line.split(",");
            Text outputKey = new Text();
            Text outputValue = new Text();

            if (indicesAndValue[0].equals("M")) {
                for (int k = 0; k < p; k++) {
                    outputKey.set(indicesAndValue[1] + "," + k);
                    outputValue.set("M," + indicesAndValue[2] + "," + indicesAndValue[3]);
                    context.write(outputKey, outputValue);
                }
            } else {
                for (int i = 0; i < m; i++) {
                    outputKey.set(i + "," + indicesAndValue[2]);
                    outputValue.set("N," + indicesAndValue[1] + "," + indicesAndValue[3]);
                    context.write(outputKey, outputValue);
                }
            }
        }
    }

    public static class MatrixReducer extends Reducer<Text, Text, Text, Text> {
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            String[] value;
            HashMap<Integer, Float> hashA = new HashMap<>();
            HashMap<Integer, Float> hashB = new HashMap<>();

            for (Text val : values) {
                value = val.toString().split(",");
                if (value[0].equals("M")) {
                    hashA.put(Integer.parseInt(value[1]), Float.parseFloat(value[2]));
                } else {
                    hashB.put(Integer.parseInt(value[1]), Float.parseFloat(value[2]));
                }
            }

            int n = Integer.parseInt(context.getConfiguration().get("n"));
            float result = 0.0f;

            for (int j = 0; j < n; j++) {
                float a_ij = hashA.containsKey(j) ? hashA.get(j) : 0.0f;
                float b_jk = hashB.containsKey(j) ? hashB.get(j) : 0.0f;
                result += a_ij * b_jk;
            }

            if (result != 0.0f) {
                context.write(null, new Text(key.toString() + "," + Float.toString(result)));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("m", "2");
        conf.set("n", "2");
        conf.set("p", "2");

        Job job = Job.getInstance(conf, "MatrixMultiplication");
        job.setJarByClass(MatMul.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setMapperClass(MatrixMapper.class);
        job.setReducerClass(MatrixReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}


/*
README Commands for Running MatMul Hadoop Job
# 1. Change to the directory containing MatMul.jar and input files
cd ~/Desktop/mat

# 2. Create an input directory in HDFS
hadoop fs -mkdir -p /matin

# 3. Copy the input matrix file to HDFS
hadoop fs -copyFromLocal matrix.txt /matin/

# 4. (Optional) List files in HDFS to confirm
hadoop fs -ls /matin

# 5. (Optional) View the input file in HDFS
hadoop fs -cat /matin/matrix.txt

# 6. Remove previous output directory if it exists
hadoop fs -rm -r /matout

# 7. Run the Hadoop job
hadoop jar MatMul.jar MatMul /matin /matout

# 8. List files in the output directory
hadoop fs -ls /matout

# 9. View the output results
hadoop fs -cat /matout/part-r-00000
  */
