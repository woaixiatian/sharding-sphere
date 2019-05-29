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

package org.apache.shardingsphere.core.optimize.result.insert;

import lombok.Getter;
import org.apache.shardingsphere.core.parse.sql.segment.dml.expr.ExpressionSegment;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Insert optimize result.
 *
 * @author panjuan
 */
@Getter
public final class InsertOptimizeResult {
    
    private final Collection<String> columnNames = new LinkedHashSet<>();
    
    private final List<InsertOptimizeResultUnit> units = new LinkedList<>();
    
    public InsertOptimizeResult(final Collection<String> columnNames) {
        this.columnNames.addAll(columnNames);
    }
    
    /**
     * Add insert optimize result uint.
     *
     * @param columnValues column values
     * @param columnParameters column parameters
     * @param startIndexOfAppendedParameters start index of appended parameters
     */
    public void addUnit(final ExpressionSegment[] columnValues, final Object[] columnParameters, final int startIndexOfAppendedParameters) {
        this.units.add(new InsertOptimizeResultUnit(columnNames, columnValues, columnParameters, startIndexOfAppendedParameters));
    }
}
