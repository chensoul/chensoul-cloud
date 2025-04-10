
/*
 *
 *  * | Licensed 未经许可不能去掉「Enjoy-iot」相关版权
 *  * +----------------------------------------------------------------------
 *  * | Author: xw2sy@163.com | Tel: 19918996474
 *  * +----------------------------------------------------------------------
 *
 *  Copyright [2025] [Enjoy-iot] | Tel: 19918996474
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package com.chensoul.mybatis.util;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.mybatisplus.annotation.DbType;
import com.chensoul.core.spring.SpringContextHolder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * JDBC 工具类
 *
 * @author EnjoyIot
 */
public class JdbcUtils {

	/**
	 * 判断连接是否正确
	 *
	 * @param url      数据源连接
	 * @param username 账号
	 * @param password 密码
	 * @return 是否正确
	 */
	public static boolean isConnectionOK(String url, String username, String password) {
		try (Connection ignored = DriverManager.getConnection(url, username, password)) {
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 获得 URL 对应的 DB 类型
	 *
	 * @param url URL
	 * @return DB 类型
	 */
	public static DbType getDbType(String url) {
		return com.baomidou.mybatisplus.extension.toolkit.JdbcUtils.getDbType(url);
	}

	/**
	 * 通过当前数据库连接获得对应的 DB 类型
	 *
	 * @return DB 类型
	 */
	public static DbType getDbType() {
		DataSource dataSource;
		try {
			DynamicRoutingDataSource dynamicRoutingDataSource = SpringContextHolder
				.getBean(DynamicRoutingDataSource.class);
			dataSource = dynamicRoutingDataSource.determineDataSource();
		} catch (NoSuchBeanDefinitionException e) {
			dataSource = SpringContextHolder.getBean(DataSource.class);
		}
		try (Connection conn = dataSource.getConnection()) {
			return DbTypeEnum.find(conn.getMetaData().getDatabaseProductName());
		} catch (SQLException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	/**
	 * 判断 JDBC 连接是否为 SQLServer 数据库
	 *
	 * @param url JDBC 连接
	 * @return 是否为 SQLServer 数据库
	 */
	public static boolean isSQLServer(String url) {
		DbType dbType = getDbType(url);
		return isSQLServer(dbType);
	}

	/**
	 * 判断 JDBC 连接是否为 SQLServer 数据库
	 *
	 * @param dbType DB 类型
	 * @return 是否为 SQLServer 数据库
	 */
	public static boolean isSQLServer(DbType dbType) {
		return Objects.equals(dbType, DbType.SQL_SERVER) || Objects.equals(dbType, DbType.SQL_SERVER2005);
	}

}
