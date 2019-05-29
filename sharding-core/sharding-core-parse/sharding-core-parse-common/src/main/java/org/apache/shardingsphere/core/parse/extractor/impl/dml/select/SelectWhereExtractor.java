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

package org.apache.shardingsphere.core.parse.extractor.impl.dml.select;

import com.google.common.base.Optional;
import org.antlr.v4.runtime.ParserRuleContext;
import org.apache.shardingsphere.core.parse.extractor.api.OptionalSQLSegmentExtractor;
import org.apache.shardingsphere.core.parse.extractor.impl.dml.WhereExtractor;
import org.apache.shardingsphere.core.parse.extractor.util.ExtractorUtils;
import org.apache.shardingsphere.core.parse.extractor.util.RuleName;
import org.apache.shardingsphere.core.parse.sql.segment.dml.WhereSegment;

import java.util.Map;

/**
 * Where extractor for select.
 *
 * @author duhongjun
 * @author zhangliang
 */
public final class SelectWhereExtractor implements OptionalSQLSegmentExtractor {
    
    private final WhereExtractor whereExtractor = new WhereExtractor();
    
    @Override
    public Optional<WhereSegment> extract(final ParserRuleContext ancestorNode, final Map<ParserRuleContext, Integer> parameterMarkerIndexes) {
        Optional<ParserRuleContext> selectItemsNode = ExtractorUtils.findFirstChildNode(ancestorNode, RuleName.SELECT_ITEMS);
        return selectItemsNode.isPresent() ? whereExtractor.extract(selectItemsNode.get().getParent(), parameterMarkerIndexes) : Optional.<WhereSegment>absent();
    }
}
