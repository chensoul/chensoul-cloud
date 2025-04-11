package com.chensoul.spring.boot.syslog.util;

import com.chensoul.core.jackson.JacksonUtils;
import com.chensoul.core.spring.WebUtils;
import com.chensoul.core.util.NetUtils;
import com.chensoul.spring.boot.syslog.annotation.SysLog;
import com.chensoul.spring.boot.syslog.annotation.SysLogIgnore;
import com.chensoul.spring.boot.syslog.event.SysLogInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class SysLogUtils {

	public SysLogInfo getSysLog(ProceedingJoinPoint joinPoint, SysLog sysLog) {
		SysLogInfo sysLogInfo = new SysLogInfo();
		sysLogInfo.setName(getSysLogValue(joinPoint, sysLog));
		sysLogInfo.setSuccess(true);
		sysLogInfo.setServerIp(NetUtils.getLocalhostStr());
		sysLogInfo.setCreatedBy(WebUtils.getUsername());
		sysLogInfo.setCreateTime(LocalDateTime.now());

		Optional<HttpServletRequest> requestOptional = WebUtils.ofRequest();
		if (requestOptional.isPresent()) {
			HttpServletRequest request = requestOptional.get();
			sysLogInfo.setRequestUrl(WebUtils.constructUrl(request));
			sysLogInfo.setRequestMethod(request.getMethod());
			sysLogInfo.setUserAgent(WebUtils.getUserAgent(request));
			sysLogInfo.setClientIp(WebUtils.getClientIp(request));

			if (HttpMethod.PUT.name().equals(sysLogInfo.getRequestMethod())
				|| HttpMethod.POST.name().equals(sysLogInfo.getRequestMethod())) {
				sysLogInfo.setRequestParams(JacksonUtils.toString(dealArgs(joinPoint.getArgs())));
			} else {
				sysLogInfo.setRequestParams(JacksonUtils.toString(request.getParameterMap()));
			}
		}
		return sysLogInfo;
	}

	private String getSysLogValue(ProceedingJoinPoint joinPoint, SysLog sysLog) {
		String value = sysLog.value();
		String expression = sysLog.expression();

		if (StringUtils.isNotBlank(expression)) {
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			EvaluationContext context = getContext(joinPoint.getArgs(), signature.getMethod());
			try {
				value = getValue(context, expression, String.class);
			} catch (Exception e) {
				log.error("@SysLog 解析 spel {} 异常", expression);
			}
		}
		return value;
	}

	private List<Object> dealArgs(Object[] args) {
		if (args == null) {
			return new ArrayList<>();
		}
		return Arrays.stream(args).filter(t -> !isFilterObject(t)).collect(Collectors.toList());
	}

	@SuppressWarnings("rawtypes")
	private boolean isFilterObject(Object o) {
		if (Objects.isNull(o)
			|| o.getClass().isAnnotationPresent(SysLogIgnore.class)
			|| o.getClass().isAnnotationPresent(PathVariable.class)) {
			return true;
		}

		Class<?> clazz = o.getClass();
		if (clazz.isArray()) {
			return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
		} else if (Collection.class.isAssignableFrom(clazz)) {
			Collection collection = (Collection) o;
			for (Object value : collection) {
				return value instanceof MultipartFile;
			}
		} else if (Map.class.isAssignableFrom(clazz)) {
			Map map = (Map) o;
			for (Object value : map.entrySet()) {
				Map.Entry entry = (Map.Entry) value;
				return entry.getValue() instanceof MultipartFile;
			}
		}
		return o instanceof MultipartFile
			|| o instanceof HttpServletRequest
			|| o instanceof HttpServletResponse
			|| o instanceof BindingResult;
	}

	/**
	 * 获取spel 定义的参数值
	 *
	 * @param context 参数容器
	 * @param key     key
	 * @param clazz   需要返回的类型
	 * @param <T>     返回泛型
	 * @return 参数值
	 */
	private <T> T getValue(EvaluationContext context, String key, Class<T> clazz) {
		SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
		Expression expression = spelExpressionParser.parseExpression(key);
		return expression.getValue(context, clazz);
	}

	/**
	 * 获取参数容器
	 *
	 * @param arguments       方法的参数列表
	 * @param signatureMethod 被执行的方法体
	 * @return 装载参数的容器
	 */
	private EvaluationContext getContext(Object[] arguments, Method signatureMethod) {
		String[] parameterNames = new StandardReflectionParameterNameDiscoverer().getParameterNames(signatureMethod);
		EvaluationContext context = new StandardEvaluationContext();
		if (parameterNames == null) {
			return context;
		}
		for (int i = 0; i < arguments.length; i++) {
			context.setVariable(parameterNames[i], arguments[i]);
		}
		return context;
	}
}
