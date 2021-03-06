/*
 *  Copyright (c) 2017 Uber Technologies, Inc. (hoodie-dev-group@uber.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package com.uber.hoodie.common.table.log;

import com.uber.hoodie.common.model.HoodieRecord;
import com.uber.hoodie.common.model.HoodieRecordPayload;
import java.util.List;
import org.apache.avro.Schema;
import org.apache.hadoop.fs.FileSystem;

public class HoodieUnMergedLogRecordScanner extends AbstractHoodieLogRecordScanner {

  private final LogRecordScannerCallback callback;

  public HoodieUnMergedLogRecordScanner(FileSystem fs, String basePath,
      List<String> logFilePaths, Schema readerSchema, String latestInstantTime,
      boolean readBlocksLazily, boolean reverseReader, int bufferSize,
      LogRecordScannerCallback callback) {
    super(fs, basePath, logFilePaths, readerSchema, latestInstantTime, readBlocksLazily, reverseReader, bufferSize);
    this.callback = callback;
  }

  @Override
  protected void processNextRecord(HoodieRecord<? extends HoodieRecordPayload> hoodieRecord) throws Exception {
    // Just call callback without merging
    callback.apply(hoodieRecord);
  }

  @Override
  protected void processNextDeletedKey(String key) {
    throw new IllegalStateException("Not expected to see delete records in this log-scan mode. Check Job Config");
  }

  @FunctionalInterface
  public static interface LogRecordScannerCallback {

    public void apply(HoodieRecord<? extends HoodieRecordPayload> record) throws Exception;
  }
}
