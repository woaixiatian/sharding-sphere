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

package org.apache.shardingsphere.core.optimize;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.core.optimize.engine.OptimizeEngine;
import org.apache.shardingsphere.core.optimize.engine.encrypt.EncryptDefaultOptimizeEngine;
import org.apache.shardingsphere.core.optimize.engine.encrypt.EncryptInsertOptimizeEngine;
import org.apache.shardingsphere.core.optimize.engine.sharding.insert.InsertOptimizeEngine;
import org.apache.shardingsphere.core.optimize.engine.sharding.query.QueryOptimizeEngine;
import org.apache.shardingsphere.core.parse.sql.statement.SQLStatement;
import org.apache.shardingsphere.core.parse.sql.statement.dml.DMLStatement;
import org.apache.shardingsphere.core.parse.sql.statement.dml.InsertStatement;
import org.apache.shardingsphere.core.parse.sql.statement.dml.SelectStatement;
import org.apache.shardingsphere.core.rule.EncryptRule;
import org.apache.shardingsphere.core.rule.ShardingRule;

import java.util.List;

/**
 * Optimize engine factory.
 *
 * @author zhangliang
 * @author maxiaoguang
 * @author panjuan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OptimizeEngineFactory {
    
    /**
     * Create optimize engine instance.
     * 
     * @param shardingRule sharding rule
     * @param sqlStatement SQL statement
     * @param parameters parameters
     * @param generatedKey generated key
     * @return optimize engine instance
     */
    public static OptimizeEngine newInstance(final ShardingRule shardingRule, final SQLStatement sqlStatement, final List<Object> parameters, final GeneratedKey generatedKey) {
        if (sqlStatement instanceof InsertStatement) {
            return new InsertOptimizeEngine(shardingRule, (InsertStatement) sqlStatement, parameters, generatedKey);
        }
        if (sqlStatement instanceof SelectStatement || sqlStatement instanceof DMLStatement) {
            return new QueryOptimizeEngine(sqlStatement.getRouteCondition(), parameters);
        }
        // TODO do with DDL and DAL
        return new QueryOptimizeEngine(sqlStatement.getRouteCondition(), parameters);
    }
    
    /**
     * Create encrypt optimize engine instance.
     * 
     * @param encryptRule encrypt rule
     * @param sqlStatement sql statement
     * @param parameters parameters
     * @return encrypt optimize engine instance
     */
    public static OptimizeEngine newInstance(final EncryptRule encryptRule, final SQLStatement sqlStatement, final List<Object> parameters) {
        if (sqlStatement instanceof InsertStatement) {
            return new EncryptInsertOptimizeEngine(encryptRule, (InsertStatement) sqlStatement, parameters);
        }
        return new EncryptDefaultOptimizeEngine();
    }
}
