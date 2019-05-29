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

package org.apache.shardingsphere.transaction.xa.jta.datasource;

import com.atomikos.beans.PropertyUtils;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.exception.ShardingException;
import org.apache.shardingsphere.transaction.xa.jta.datasource.properties.XAPropertiesFactory;
import org.apache.shardingsphere.transaction.xa.jta.datasource.swapper.DataSourceSwapper;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * XA data source factory.
 *
 * @author zhaojun
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class XADataSourceFactory {
    
    private static final Multimap<DatabaseType, String> XA_DRIVER_CLASS_NAMES = LinkedHashMultimap.create();
    
    private static final DataSourceSwapper SWAPPER = new DataSourceSwapper();
    
    static {
        XA_DRIVER_CLASS_NAMES.put(DatabaseType.H2, "org.h2.jdbcx.JdbcDataSource");
        XA_DRIVER_CLASS_NAMES.put(DatabaseType.MySQL, "com.mysql.jdbc.jdbc2.optional.MysqlXADataSource");
        XA_DRIVER_CLASS_NAMES.put(DatabaseType.MySQL, "com.mysql.cj.jdbc.MysqlXADataSource");
        XA_DRIVER_CLASS_NAMES.put(DatabaseType.PostgreSQL, "org.postgresql.xa.PGXADataSource");
        XA_DRIVER_CLASS_NAMES.put(DatabaseType.Oracle, "oracle.jdbc.xa.client.OracleXADataSource");
        XA_DRIVER_CLASS_NAMES.put(DatabaseType.SQLServer, "com.microsoft.sqlserver.jdbc.SQLServerXADataSource");
    }
    
    /**
     * Create XA DataSource instance.
     *
     * @param databaseType database type
     * @return XA DataSource instance
     */
    public static XADataSource build(final DatabaseType databaseType) {
        return createXADataSource(databaseType);
    }
    
    /**
     * Create XA data source through general data source.
     *
     * @param databaseType database type
     * @param dataSource data source
     * @return XA data source
     */
    @SneakyThrows
    public static XADataSource build(final DatabaseType databaseType, final DataSource dataSource) {
        XADataSource result = createXADataSource(databaseType);
        Properties xaProperties = XAPropertiesFactory.createXAProperties(databaseType).build(SWAPPER.swap(dataSource));
        PropertyUtils.setProperties(result, xaProperties);
        return result;
    }
    
    private static XADataSource createXADataSource(final DatabaseType databaseType) {
        XADataSource result = null;
        List<ShardingException> exceptions = new LinkedList<>();
        for (String each : XA_DRIVER_CLASS_NAMES.get(databaseType)) {
            try {
                result = loadXADataSource(each);
            } catch (final ShardingException ex) {
                exceptions.add(ex);
            }
        }
        if (null == result && !exceptions.isEmpty()) {
            if (exceptions.size() > 1) {
                throw new ShardingException("Failed to create [%s] XA DataSource", databaseType);
            } else {
                throw exceptions.iterator().next();
            }
        }
        return result;
    }
    
    private static XADataSource loadXADataSource(final String xaDataSourceClassName) {
        Class xaDataSourceClass;
        try {
            xaDataSourceClass = Thread.currentThread().getContextClassLoader().loadClass(xaDataSourceClassName);
        } catch (final ClassNotFoundException ignored) {
            try {
                xaDataSourceClass = Class.forName(xaDataSourceClassName);
            } catch (final ClassNotFoundException ex) {
                throw new ShardingException("Failed to load [%s]", xaDataSourceClassName);
            }
        }
        try {
            return (XADataSource) xaDataSourceClass.newInstance();
        } catch (final InstantiationException | IllegalAccessException ex) {
            throw new ShardingException("Failed to instance [%s]", xaDataSourceClassName);
        }
    }
}
