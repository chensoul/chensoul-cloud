package com.chensoul.core;

/**
 * 缓存的key 常量
 *
 * @author <a href="mailto:ichensoul@gmail.com">chensoul</a>
 */
public interface CacheConstants {

    /**
     * 菜单信息缓存的key
     */
    String MENU_DETAIL = "rose:menu_detail";

    /**
     * 用户信息缓存的key
     */
    String USER_DETAIL = "rose:user_detail";

    /**
     * 字典信息缓存的key
     */
    String DICT_DETAIL = "rose:dict_detail";

    /**
     * oauth 客户端缓存的key，值为hash
     */
    String OAUTH_CLIENT_DETAIL = "rose:oauth_client_detail";

    /**
     * tokens缓存的key
     */
    String OAUTH_TOKENS = "rose:oauth_tokens";

    /**
     * oauth token store 存储前缀
     */
    String OAUTH_TOKEN_STORE_PREFIX = "rose:oauth_token_store:";

    String SHOP = "rose:shop";

    String TENANT_APP_CONFIG = "rose:tenant_app_config";

    String CLIENT = "rose:client";

    String LOGIN_FAIL_COUNT_CACHE = "rose:loginFailCount:";
}
