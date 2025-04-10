
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
package com.chensoul.spring.boot.redis.mq.stream;

import com.chensoul.core.jackson.JacksonUtils;
import com.chensoul.core.spring.ReflectionUtils;
import com.chensoul.spring.boot.redis.mq.RedisMQTemplate;
import com.chensoul.spring.boot.redis.mq.interceptor.RedisMessageInterceptor;
import com.chensoul.spring.boot.redis.mq.message.AbstractRedisMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.stream.StreamListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Redis Stream 监听器抽象类，用于实现集群消费
 *
 * @param <T> 消息类型。一定要填写噢，不然会报错
 * @author EnjoyIot
 */
public abstract class AbstractRedisStreamMessageListener<T extends AbstractRedisStreamMessage>
	implements StreamListener<String, ObjectRecord<String, String>> {

	/**
	 * 消息类型
	 */
	private final Class<T> messageType;

	/**
	 * Redis Channel
	 */
	@Getter
	private final String streamKey;

	/**
	 * Redis 消费者分组，默认使用 spring.application.name 名字
	 */
	@Value("${spring.application.name}")
	@Getter
	private String group;

	/**
	 * RedisMQTemplate
	 */
	@Setter
	private RedisMQTemplate redisMQTemplate;

	@SneakyThrows
	protected AbstractRedisStreamMessageListener() {
		this.messageType = getMessageClass();
		this.streamKey = messageType.getDeclaredConstructor().newInstance().getStreamKey();
	}

	@Override
	public void onMessage(ObjectRecord<String, String> message) {
		// 消费消息
		T messageObj = JacksonUtils.fromString(message.getValue(), messageType);
		try {
			consumeMessageBefore(messageObj);
			// 消费消息
			this.onMessage(messageObj);
			// ack 消息消费完成
			redisMQTemplate.getRedisTemplate().opsForStream().acknowledge(group, message);
			// TODO 需要额外考虑以下几个点：
			// 1. 处理异常的情况
			// 2. 发送日志；以及事务的结合
			// 3. 消费日志；以及通用的幂等性
			// 4. 消费失败的重试，https://zhuanlan.zhihu.com/p/60501638
		} finally {
			consumeMessageAfter(messageObj);
		}
	}

	/**
	 * 处理消息
	 *
	 * @param message 消息
	 */
	public abstract void onMessage(T message);

	/**
	 * 通过解析类上的泛型，获得消息类型
	 *
	 * @return 消息类型
	 */
	@SuppressWarnings("unchecked")
	private Class<T> getMessageClass() {
		Type type = ReflectionUtils.getTypeArgument(getClass(), 0);
		if (type == null) {
			throw new IllegalStateException(String.format("类型(%s) 需要设置消息类型", getClass().getName()));
		}
		return (Class<T>) type;
	}

	private void consumeMessageBefore(AbstractRedisMessage message) {
		assert redisMQTemplate != null;
		List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
		// 正序
		interceptors.forEach(interceptor -> interceptor.consumeMessageBefore(message));
	}

	private void consumeMessageAfter(AbstractRedisMessage message) {
		assert redisMQTemplate != null;
		List<RedisMessageInterceptor> interceptors = redisMQTemplate.getInterceptors();
		// 倒序
		for (int i = interceptors.size() - 1; i >= 0; i--) {
			interceptors.get(i).consumeMessageAfter(message);
		}
	}

}
