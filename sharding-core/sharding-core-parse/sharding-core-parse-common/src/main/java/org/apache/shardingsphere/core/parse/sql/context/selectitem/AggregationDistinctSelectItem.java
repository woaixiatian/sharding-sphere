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

package org.apache.shardingsphere.core.parse.sql.context.selectitem;

import com.google.common.base.Optional;
import lombok.Getter;
import org.apache.shardingsphere.core.constant.AggregationType;

/**
 * Aggregation distinct select item.
 *
 * @author panjuan
 */
@Getter
public final class AggregationDistinctSelectItem extends AggregationSelectItem {
    
    private final String distinctColumnName;
    
    public AggregationDistinctSelectItem(final AggregationType type, final String innerExpression, final Optional<String> alias, final String distinctColumnName) {
        super(type, innerExpression, alias);
        this.distinctColumnName = distinctColumnName;
    }
    
    /**
     * Get distinct column label.
     *
     * @return distinct column label
     */
    public String getDistinctColumnLabel() {
        return getAlias().isPresent() ? getAlias().get() : distinctColumnName;
    }
}
