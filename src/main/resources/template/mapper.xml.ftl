<#--@formatter:off-->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${package.Mapper}.${table.mapperName}">
    <#if enableCache>
        <cache type="com.guanwei.config.MybatisRedisCache"/>
    </#if>
    <#if baseResultMap>

    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${package.Entity}.${entity}">
    <#list table.tableFields as field>
        <#if field.keyFlag><#--生成主键排在第一位-->
        <id column="${field.name}" property="${field.propertyName}"/>
        </#if>
    </#list>
    <#list table.commonFields as field><#--生成公共字段 -->
        <result column="${field.name}" property="${field.propertyName}"/>
    </#list>
    <#list table.tableFields as field>
        <#if !field.keyFlag><#--生成普通字段 -->
        <result column="${field.name}" property="${field.propertyName}"/>
        </#if>
    </#list>
    </resultMap>
    </#if>
    <#if baseColumnList>

    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
    <#list table.tableFields as field>
        <#if field_index = 0>
            ${field.name}
        <#else>
            ,${field.name}
        </#if>
    </#list>
    </sql>
    </#if>

    <select id="get${entity}List" resultType="${package.DTO}.${entity}DTO">
        SELECT *
        FROM ${table.name}
        <where>
        <#list table.tableFields as field>
            <if test="@com.guanwei.core.utils.EmptyUtil@isNotEmpty(${field.name?uncap_first})">
            <#if field_index = 0>
                ${field.name?uncap_first} = ${'#'}${'{'}${field.name?uncap_first}${'}'}
            <#else>
                AND ${field.name?uncap_first} = ${'#'}${'{'}${field.name?uncap_first}${'}'}
            </#if>
            </if>
        </#list>
        </where>
    </select>
</mapper>
<#--@formatter:on-->
