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

package org.apache.shardingsphere.core.parse.integrate.asserts.item;

import org.apache.shardingsphere.core.parse.integrate.asserts.SQLStatementAssertMessage;
import org.apache.shardingsphere.core.parse.integrate.jaxb.item.ExpectedSelectItems;
import org.apache.shardingsphere.core.parse.sql.context.selectitem.SelectItem;

import java.util.Set;

/**
 * Item assert.
 *
 * @author zhangliang
 * @author panjuan
 */
public final class ItemAssert {
    
    private final AggregationSelectItemAssert aggregationSelectItemAssert;
    
    private final DistinctSelectItemAssert distinctSelectItemAssert;
    
    public ItemAssert(final SQLStatementAssertMessage assertMessage) {
        aggregationSelectItemAssert = new AggregationSelectItemAssert(assertMessage);
        distinctSelectItemAssert = new DistinctSelectItemAssert(assertMessage);
    }
    
    /**
     * Assert items.
     * 
     * @param actual actual items
     * @param expected expected items
     */
    public void assertItems(final Set<SelectItem> actual, final ExpectedSelectItems expected) {
        // TODO assert SelectItems total size
        // TODO assert StarSelectItem
        // TODO assert CommonSelectItem
        aggregationSelectItemAssert.assertAggregationSelectItems(actual, expected.getAggregationSelectItems());
        distinctSelectItemAssert.assertDistinctSelectItems(actual, expected.getDistinctSelectItem());
    }
}
