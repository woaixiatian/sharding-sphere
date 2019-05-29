/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.core.parse.filler.encrypt.dml.insert;

import com.google.common.base.Optional;
import org.apache.shardingsphere.core.parse.filler.api.SQLSegmentFiller;
import org.apache.shardingsphere.core.parse.sql.context.insertvalue.InsertValue;
import org.apache.shardingsphere.core.parse.sql.segment.dml.InsertValuesSegment;
import org.apache.shardingsphere.core.parse.sql.statement.SQLStatement;
import org.apache.shardingsphere.core.parse.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.core.parse.sql.token.impl.InsertValuesToken;

/**
 * Insert values filler for encrypt.
 *
 * @author zhangliang
 * @author panjuan
 */
public final class EncryptInsertValuesFiller implements SQLSegmentFiller<InsertValuesSegment> {
    
    @Override
    public void fill(final InsertValuesSegment sqlSegment, final SQLStatement sqlStatement) {
        InsertStatement insertStatement = (InsertStatement) sqlStatement;
        InsertValue insertValue = new InsertValue(sqlSegment.getValues());
        insertStatement.getValues().add(insertValue);
        insertStatement.setParametersIndex(insertStatement.getParametersIndex() + insertValue.getParametersCount());
        fillWithInsertValuesToken(sqlSegment, insertStatement);
    }
    
    private void fillWithInsertValuesToken(final InsertValuesSegment sqlSegment, final InsertStatement insertStatement) {
        Optional<InsertValuesToken> insertValuesToken = insertStatement.findSQLToken(InsertValuesToken.class);
        if (insertValuesToken.isPresent()) {
            int startIndex = insertValuesToken.get().getStartIndex() < sqlSegment.getStartIndex() ? insertValuesToken.get().getStartIndex() : sqlSegment.getStartIndex();
            int stopIndex = insertValuesToken.get().getStopIndex() > sqlSegment.getStopIndex() ? insertValuesToken.get().getStopIndex() : sqlSegment.getStopIndex();
            insertStatement.getSQLTokens().remove(insertValuesToken.get());
            insertStatement.getSQLTokens().add(new InsertValuesToken(startIndex, stopIndex));
        } else {
            insertStatement.getSQLTokens().add(new InsertValuesToken(sqlSegment.getStartIndex(), sqlSegment.getStopIndex()));
        }
    }
}
