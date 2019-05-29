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

package org.apache.shardingsphere.core.parse.filler.sharding.dml.insert;

import com.google.common.base.Optional;
import lombok.Setter;
import org.apache.shardingsphere.core.parse.filler.api.SQLSegmentFiller;
import org.apache.shardingsphere.core.parse.filler.api.ShardingRuleAwareFiller;
import org.apache.shardingsphere.core.parse.sql.context.condition.AndCondition;
import org.apache.shardingsphere.core.parse.sql.context.condition.Column;
import org.apache.shardingsphere.core.parse.sql.context.condition.Condition;
import org.apache.shardingsphere.core.parse.sql.context.insertvalue.InsertValue;
import org.apache.shardingsphere.core.parse.sql.segment.dml.InsertValuesSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.simple.SimpleExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.statement.SQLStatement;
import org.apache.shardingsphere.core.parse.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.core.parse.sql.token.impl.InsertColumnsToken;
import org.apache.shardingsphere.core.parse.sql.token.impl.InsertValuesToken;
import org.apache.shardingsphere.core.rule.ShardingRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Insert values filler for sharding.
 *
 * @author zhangliang
 * @author panjuan
 */
@Setter
public final class ShardingInsertValuesFiller implements SQLSegmentFiller<InsertValuesSegment>, ShardingRuleAwareFiller {
    
    private ShardingRule shardingRule;
    
    @Override
    public void fill(final InsertValuesSegment sqlSegment, final SQLStatement sqlStatement) {
        InsertStatement insertStatement = (InsertStatement) sqlStatement;
        AndCondition andCondition = new AndCondition();
        Iterator<String> columnNames = getColumnNames(sqlSegment, insertStatement);
        for (ExpressionSegment each : sqlSegment.getValues()) {
            if (each instanceof SimpleExpressionSegment) {
                fillShardingCondition(andCondition, insertStatement.getTables().getSingleTableName(), columnNames.next(), (SimpleExpressionSegment) each);
            }
        }
        insertStatement.getRouteCondition().getOrConditions().add(andCondition);
        InsertValue insertValue = new InsertValue(sqlSegment.getValues());
        insertStatement.getValues().add(insertValue);
        insertStatement.setParametersIndex(insertStatement.getParametersIndex() + insertValue.getParametersCount());
        fillWithInsertValuesToken(sqlSegment, insertStatement);
        reviseInsertColumnNames(sqlSegment, insertStatement);
    }
    
    private Iterator<String> getColumnNames(final InsertValuesSegment sqlSegment, final InsertStatement insertStatement) {
        Collection<String> result = new ArrayList<>(insertStatement.getColumnNames());
        result.removeAll(shardingRule.getShardingEncryptorEngine().getAssistedQueryColumns(insertStatement.getTables().getSingleTableName()));
        Optional<String> generateKeyColumnName = shardingRule.findGenerateKeyColumnName(insertStatement.getTables().getSingleTableName());
        if (insertStatement.getColumnNames().size() != sqlSegment.getValues().size() && generateKeyColumnName.isPresent()) {
            result.remove(generateKeyColumnName.get());
        }
        return result.iterator();
    }
    
    private void fillShardingCondition(final AndCondition andCondition, final String tableName, final String columnName, final SimpleExpressionSegment expressionSegment) {
        if (shardingRule.isShardingColumn(columnName, tableName)) {
            andCondition.getConditions().add(new Condition(new Column(columnName, tableName), expressionSegment));
        }
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
    
    private void reviseInsertColumnNames(final InsertValuesSegment sqlSegment, final InsertStatement insertStatement) {
        Collection<String> result = new ArrayList<>(insertStatement.getColumnNames());
        result.removeAll(shardingRule.getShardingEncryptorEngine().getAssistedQueryColumns(insertStatement.getTables().getSingleTableName()));
        Optional<String> generateKeyColumnName = shardingRule.findGenerateKeyColumnName(insertStatement.getTables().getSingleTableName());
        if (insertStatement.getColumnNames().size() != sqlSegment.getValues().size() && generateKeyColumnName.isPresent()) {
            result.remove(generateKeyColumnName.get());
            reviseInsertColumnsToken(insertStatement, generateKeyColumnName.get(), result);
        }
        insertStatement.getColumnNames().clear();
        insertStatement.getColumnNames().addAll(result);
    }
    
    private void reviseInsertColumnsToken(final InsertStatement insertStatement, final String generateKeyColumnName, final Collection<String> columnNames) {
        Optional<InsertColumnsToken> insertColumnsToken = insertStatement.findSQLToken(InsertColumnsToken.class);
        Collection<String> assistedColumns = new LinkedList<>(insertColumnsToken.get().getColumns());
        assistedColumns.removeAll(columnNames);
        assistedColumns.remove(generateKeyColumnName);
        insertColumnsToken.get().getColumns().clear();
        insertColumnsToken.get().getColumns().addAll(columnNames);
        insertColumnsToken.get().getColumns().add(generateKeyColumnName);
        insertColumnsToken.get().getColumns().addAll(assistedColumns);
    }
}
