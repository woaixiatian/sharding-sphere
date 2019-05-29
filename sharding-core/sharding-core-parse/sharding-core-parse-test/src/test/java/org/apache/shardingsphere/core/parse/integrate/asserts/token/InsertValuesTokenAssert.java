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

package org.apache.shardingsphere.core.parse.integrate.asserts.token;

import com.google.common.base.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.core.parse.integrate.asserts.SQLStatementAssertMessage;
import org.apache.shardingsphere.core.parse.integrate.jaxb.token.ExpectedInsertValuesToken;
import org.apache.shardingsphere.core.parse.integrate.jaxb.token.ExpectedTokens;
import org.apache.shardingsphere.core.parse.sql.token.SQLToken;
import org.apache.shardingsphere.core.parse.sql.token.impl.InsertValuesToken;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Multiple insert values token assert.
 *
 * @author maxiaoguang
 */
@RequiredArgsConstructor
final class InsertValuesTokenAssert {
    
    private final SQLStatementAssertMessage assertMessage;
    
    void assertInsertValuesToken(final Collection<SQLToken> actual, final ExpectedTokens expected) {
        Optional<InsertValuesToken> insertValuesToken = getInsertValuesToken(actual);
        if (insertValuesToken.isPresent()) {
            assertInsertValuesToken(insertValuesToken.get(), expected.getInsertValuesToken());
        } else {
            assertNull(assertMessage.getFullAssertMessage("Insert values token should not exist: "), expected.getInsertValuesToken());
        }
    }
    
    private void assertInsertValuesToken(final InsertValuesToken actual, final ExpectedInsertValuesToken expected) {
        assertThat(assertMessage.getFullAssertMessage("Insert values token begin position assertion error: "), actual.getStartIndex(), is(expected.getBeginPosition()));
    }
    
    private Optional<InsertValuesToken> getInsertValuesToken(final Collection<SQLToken> actual) {
        for (SQLToken each : actual) {
            if (each instanceof InsertValuesToken) {
                return Optional.of((InsertValuesToken) each);
            }
        }
        return Optional.absent();
    }
}
