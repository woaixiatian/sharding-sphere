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

package org.apache.shardingsphere.core.optimize.engine.encrypt;

import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.optimize.engine.OptimizeEngine;
import org.apache.shardingsphere.core.optimize.result.OptimizeResult;
import org.apache.shardingsphere.core.optimize.result.insert.InsertOptimizeResult;
import org.apache.shardingsphere.core.optimize.result.insert.InsertOptimizeResultUnit;
import org.apache.shardingsphere.core.parse.sql.context.insertvalue.InsertValue;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.ExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.simple.LiteralExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.simple.ParameterMarkerExpressionSegment;
import org.apache.shardingsphere.core.parse.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.core.rule.EncryptRule;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Encrypt insert optimize engine.
 *
 * @author panjuan
 */
@RequiredArgsConstructor
public final class EncryptInsertOptimizeEngine implements OptimizeEngine {
    
    private final EncryptRule encryptRule;
    
    private final InsertStatement insertStatement;
    
    private final List<Object> parameters;
    
    @Override
    public OptimizeResult optimize() {
        List<InsertValue> insertValues = insertStatement.getValues();
        InsertOptimizeResult insertOptimizeResult = new InsertOptimizeResult(insertStatement.getColumnNames());
        int parametersCount = 0;
        int insertOptimizeResultIndex = 0;
        for (InsertValue each : insertValues) {
            ExpressionSegment[] currentColumnValues = createCurrentColumnValues(each);
            Object[] currentParameters = createCurrentParameters(parametersCount, each);
            parametersCount = parametersCount + each.getParametersCount();
            insertOptimizeResult.addUnit(currentColumnValues, currentParameters, each.getParametersCount());
            if (isNeededToAppendQueryAssistedColumn()) {
                fillWithQueryAssistedColumn(insertOptimizeResult, insertOptimizeResultIndex);
            }
            insertOptimizeResultIndex++;
        }
        return new OptimizeResult(insertOptimizeResult);
    }
    
    private ExpressionSegment[] createCurrentColumnValues(final InsertValue insertValue) {
        ExpressionSegment[] result = new ExpressionSegment[insertValue.getAssignments().size() + getIncrement()];
        insertValue.getAssignments().toArray(result);
        return result;
    }
    
    private Object[] createCurrentParameters(final int beginIndex, final InsertValue insertValue) {
        if (0 == insertValue.getParametersCount()) {
            return new Object[0];
        }
        Object[] result = new Object[insertValue.getParametersCount() + getIncrement()];
        parameters.subList(beginIndex, beginIndex + insertValue.getParametersCount()).toArray(result);
        return result;
    }
    
    private int getIncrement() {
        int result = 0;
        if (isNeededToAppendQueryAssistedColumn()) {
            result += encryptRule.getEncryptorEngine().getAssistedQueryColumnCount(insertStatement.getTables().getSingleTableName());
        }
        return result;
    }
    
    private boolean isNeededToAppendQueryAssistedColumn() {
        return encryptRule.getEncryptorEngine().isHasShardingQueryAssistedEncryptor(insertStatement.getTables().getSingleTableName());
    }
    
    private void fillWithQueryAssistedColumn(final InsertOptimizeResult insertOptimizeResult, final int insertOptimizeResultIndex) {
        Collection<String> assistedColumnNames = new LinkedList<>();
        for (String each : insertOptimizeResult.getColumnNames()) {
            InsertOptimizeResultUnit unit = insertOptimizeResult.getUnits().get(insertOptimizeResultIndex);
            Optional<String> assistedColumnName = encryptRule.getEncryptorEngine().getAssistedQueryColumn(insertStatement.getTables().getSingleTableName(), each);
            if (assistedColumnName.isPresent()) {
                assistedColumnNames.add(assistedColumnName.get());
                fillInsertOptimizeResultUnit(unit, (Comparable<?>) unit.getColumnValue(each));
            }
        }
        if (!assistedColumnNames.isEmpty()) {
            insertOptimizeResult.getColumnNames().addAll(assistedColumnNames);
        }
    }
    
    private void fillInsertOptimizeResultUnit(final InsertOptimizeResultUnit unit, final Comparable<?> columnValue) {
        if (!parameters.isEmpty()) {
            // TODO fix start index and stop index
            unit.addColumnValue(new ParameterMarkerExpressionSegment(0, 0, parameters.size() - 1));
            unit.addColumnParameter(columnValue);
        } else {
            // TODO fix start index and stop index
            unit.addColumnValue(new LiteralExpressionSegment(0, 0, columnValue));
        }
    }
}
