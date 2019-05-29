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

package org.apache.shardingsphere.core.parse.sql.token.impl;

import lombok.Getter;
import lombok.ToString;
import org.apache.shardingsphere.core.parse.sql.context.condition.Column;
import org.apache.shardingsphere.core.parse.sql.token.SQLToken;
import org.apache.shardingsphere.core.parse.sql.token.Substitutable;

/**
 * Encrypt column token.
 *
 * @author panjuan
 */
@Getter
@ToString
public final class EncryptColumnToken extends SQLToken implements Substitutable {
    
    private final Column column;
    
    private final boolean isInWhere;
    
    private final int stopIndex;
    
    public EncryptColumnToken(final int startIndex, final int stopIndex, final Column column, final boolean isInWhere) {
        super(startIndex);
        this.column = column;
        this.isInWhere = isInWhere;
        this.stopIndex = stopIndex;
    }
}
