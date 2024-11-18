package ${table.entityPackagePath};

${table.tableName}
${global.author}

public class ${table.entityClassName} {

<#list table.tableFields as field>

    <#if field.comment!?length gt 0>
        /**
        * ${field.comment}
        */
    </#if>
    private ${field.propertyType} ${field.propertyName};
</#list>
}
