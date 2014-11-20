package com.tinkerpop.gremlin.giraph.structure.io.kryo;

import com.tinkerpop.gremlin.giraph.process.computer.GiraphComputeVertex;
import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class KryoInputFormat extends FileInputFormat<NullWritable, GiraphComputeVertex> implements Configurable {

    private Configuration config;

    @Override
    public RecordReader<NullWritable, GiraphComputeVertex> createRecordReader(final InputSplit split, final TaskAttemptContext context) throws IOException, InterruptedException {
        RecordReader<NullWritable, GiraphComputeVertex> reader = new KryoRecordReader();
        reader.initialize(split, context);
        return reader;
    }

    @Override
    protected boolean isSplitable(final JobContext context, final Path file) {
        return null == new CompressionCodecFactory(context.getConfiguration()).getCodec(file);
    }

    @Override
    public void setConf(final Configuration config) {
        this.config = config;
    }

    @Override
    public Configuration getConf() {
        return this.config;
    }
}
