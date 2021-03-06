/*
 * Copyright 2010-2013 10gen Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.hadoop.hive.output;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.FileSinkOperator.RecordWriter;
import org.apache.hadoop.hive.ql.io.HiveOutputFormat;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Progressable;

import com.mongodb.DBCollection;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.hadoop.mapred.output.MongoRecordWriter;
import com.mongodb.hadoop.util.MongoConfigUtil;

/*
 * Define a HiveMongoOutputFormat that specifies how Hive should write data in
 * Hive tables into MongoDB
 */
public class HiveMongoOutputFormat implements HiveOutputFormat<BSONWritable, BSONWritable>  {
    
    @Override
    public RecordWriter getHiveRecordWriter(final JobConf conf,
                        Path finalOutPath,
                        Class<? extends Writable> valueClass,
                        boolean isCompressed,
                        Properties tableProperties,
                        Progressable progress) throws IOException {
        return new HiveMongoRecordWriter(MongoConfigUtil.getOutputCollections(conf), conf);
    }
    
    
    @Override
    public void checkOutputSpecs(FileSystem arg0, JobConf arg1)
            throws IOException {
    }
    
    
    @Override
    public org.apache.hadoop.mapred.RecordWriter<BSONWritable, BSONWritable> 
            getRecordWriter(FileSystem arg0, 
                JobConf arg1, 
                String arg2,
                Progressable arg3) throws IOException {
        throw new IOException("Hive should call 'getHiveRecordWriter' instead of 'getRecordWriter'");
    }
    

    /*
     * HiveMongoRecordWriter ->
     * MongoRecordWriter used to write from Hive into BSON Objects
     */
    private class HiveMongoRecordWriter 
            extends MongoRecordWriter<Object, BSONWritable>
            implements RecordWriter {
    
        public HiveMongoRecordWriter(List<DBCollection> ls, JobConf conf) {
            super(ls, conf);
        }
        
        @Override
        public void close(boolean abort) throws IOException {
            super.close(null);
        }
        
        @Override
        public void write(Writable w) throws IOException {
            super.write(null, (BSONWritable) w);
        }
    }
}
