/*
 * Copyright (c) 2023 WEMIRR-PLATFORM Authors. All Rights Reserved.
 *
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

package com.chensoul.spring.boot.oss.old.storage.cloud.qiniu;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import com.chensoul.spring.boot.oss.old.storage.QiNiuOssOperation;

import static com.chensoul.spring.boot.oss.old.storage.OssOperation.OSS_CONFIG_PREFIX_QINIU;
import static com.chensoul.spring.boot.oss.old.storage.OssOperation.QI_NIU_OSS_OPERATION;

import com.chensoul.spring.boot.oss.old.storage.cloud.qiniu.connection.QiNiuConnectionFactory;
import com.chensoul.spring.boot.oss.old.storage.cloud.qiniu.connection.QiNiuOssClientConnectionFactory;
import com.chensoul.spring.boot.oss.old.storage.properties.QiNiuOssProperties;

/**
 * @author Levin
 */
@EnableConfigurationProperties({ QiNiuOssProperties.class })
@ConditionalOnProperty(prefix = OSS_CONFIG_PREFIX_QINIU, name = "enabled", havingValue = "true")
public class QiNiuOssAutoConfiguration {

	@Bean
	public QiNiuConnectionFactory qiNiuConnectionFactory(QiNiuOssProperties properties) {
		return new QiNiuOssClientConnectionFactory(properties);
	}

	@Bean(QI_NIU_OSS_OPERATION)
	public QiNiuOssOperation qiNiuStorageOperation(QiNiuOssProperties properties,
			QiNiuConnectionFactory qiNiuConnectionFactory) {
		return new QiNiuOssOperation(properties, qiNiuConnectionFactory);
	}

}
