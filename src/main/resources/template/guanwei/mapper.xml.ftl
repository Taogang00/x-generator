<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${table.mapperPackagePath}.${table.mapperClassName}">
    <!-- 通用查询映射结果 -->
    <resultMap id="BaseResultMap" type="${table.entityPackagePath}.${table.entityClassName}">
    <#assign hasProcessed = false />
    <#list table.tableFields as field>
        <#if !hasProcessed>
        <#if field.primaryKey><#--生成主键排在第一位-->
        <id column="${field.propertyName}" property="${field.propertyName}"/>
        <#assign hasProcessed = true />
        </#if>
        </#if>
    </#list>
    <#list table.tableFields as field>
        <#if !field.primaryKey><#--生成普通字段 -->
        <result column="${field.propertyName}" property="${field.propertyName}"/>
        </#if>
    </#list>
    </resultMap>

    <!-- 通用查询结果列 -->
    <sql id="Base_Column_List">
    <#list table.tableFields?chunk(8) as group>
        <#list group as field><#if field_index != 0 || !group?is_first>,</#if>${field.propertyName}</#list>
    </#list>
    </sql>

    <select id="get${table.entityClassName}List" resultType="${table.dtoPackagePath}.${table.dtoClassName}">
        SELECT *
        FROM ${table.tableName}
        <where>
        <#list table.tableFields as field>
            <if test="@com.guanwei.core.utils.EmptyUtil@isNotEmpty(${field.propertyName?uncap_first})">
                AND ${field.propertyName?uncap_first} = ${'#'}${'{'}${field.propertyName?uncap_first}${'}'}
            </if>
        </#list>
        </where>
    </select>
</mapper>
