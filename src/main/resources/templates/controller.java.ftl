package ${package.Controller};


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import lombok.AllArgsConstructor;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import java.util.List;

import ${package.Mapper}.${table.mapperName};
import ${package.Entity}.${entity};
<#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
import ${cfg.poPackage}.${entity}Po;
</#if>

/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Api(tags = {"${table.comment}API"})
@AllArgsConstructor
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("/<#if (cfg.controllerUrlPrefix!'') != '' >${cfg.controllerUrlPrefix}</#if><#if package.ModuleName?? && package.ModuleName != "">/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>

    private final ${table.mapperName} mapper;

    @ApiOperation("新增${table.comment}")
    @PostMapping("/create")
    public void add(@RequestBody ${entity} model){
        mapper.insert(model);
    }


    @ApiOperation("删除${table.comment}")
    @ApiImplicitParam(value = "id", name = "id", dataTypeClass = Long.class, paramType = "query", required = true)
    @DeleteMapping("/del")
    public void del(Long id){
        mapper.deleteById(id);
    }


    @ApiOperation("查找${table.comment}")
    @ApiImplicitParam(value = "id", name = "id", dataTypeClass = Long.class, paramType = "path", required = true)
    @GetMapping("/{id}")
    public ${entity} get(@PathVariable("id") Long id){
        return mapper.selectById(id);
    }

    @ApiOperation("修改${table.comment}")
    @PutMapping("/update")
    public void update(@RequestBody ${entity} model){
        mapper.updateById(model);
    }

    @ApiOperation("${table.comment}列表")
    @PostMapping("/list")
    <#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
    public List<${entity}Po> list(@RequestBody ${entity}Po entity){
        return mapper.selectPoList(this.buildQueryObject(entity));
    }
    <#else >
    public List<${entity}> list(@RequestBody ${entity} entity){
        return mapper.selectList(this.buildQueryObject(entity));
    }
    </#if>

    @ApiOperation("分页查询")
    @GetMapping("/page")
    <#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
    public IPage<${entity}Po> pageList(${entity}Po entity, int size, int current){
        QueryWrapper<${entity}Po> query = this.buildQueryObject(entity);
        Page<${entity}Po> page = new Page<>(current, size);
        IPage<${entity}Po> result = mapper.selectPoPage(page, query);
        return result;
    }
    <#else >
    public IPage<${entity}> pageList(${entity} entity, int size, int current){
        QueryWrapper<${entity}> query = this.buildQueryObject(entity);
        Page<${entity}> page = new Page<>(current, size);
        IPage<${entity}> result = mapper.selectPage(page, query);
        return result;
    }
    </#if>


    <#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
    private QueryWrapper<${entity}Po> buildQueryObject(${entity}Po entity){
        QueryWrapper<${entity}Po> query = new QueryWrapper<>();
        <#list cfg['${table.name}:fields'] as field>
            <#if field.fields ??>
        if(entity.get${field.propertyName ? cap_first}() != null){
                   <#list field.fields as joinTableField>
            if(entity.get${field.propertyName ? cap_first}().get${joinTableField.propertyName ? cap_first}() != null){
                query.eq("${field.name}.${joinTableField.name}", entity.get${field.propertyName ? cap_first}().get${joinTableField.propertyName ? cap_first}());
            }
               </#list>
        }
            <#else>
        if(entity.get${field.propertyName ? cap_first}() != null){
            query.eq("${table.name + "." + field.name}", entity.get${field.propertyName ? cap_first}());
        }
            </#if>
        </#list>
        return query;
    }
    <#else >
    private QueryWrapper<${entity}> buildQueryObject(${entity} entity){
        QueryWrapper<${entity}> query = new QueryWrapper<>();
        <#list table.fields as field>
            if(entity.get${field.propertyName ? cap_first}() != null){
            query.eq("${field.name}", entity.get${field.propertyName ? cap_first}());
            }
        </#list>
        return query;
    }
    </#if>


}
</#if>
