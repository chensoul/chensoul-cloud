package com.chensoul.spring.boot.oss.rule;

/**
 * Oss通用规则
 *
 * @author Chill
 */
public interface OssRule {

    /**
     * 获取存储桶规则
     *
     * @param bucketName 存储桶名称
     * @return String
     */
    String bucketName(String bucketName);

    /**
     * 获取文件名规则
     *
     * @param originalFilename 文件名
     * @return String
     */
    String fileName(String originalFilename);
}
