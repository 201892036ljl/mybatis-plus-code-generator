package ${package.Mapper};

import org.apache.ibatis.annotations.Mapper;
import ${package.Entity}.${entity};
<#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
import ${cfg.poPackage}.${entity}Po;
import senrui.mybatisplus.BaseMapper;
import java.util.List;
<#else>
import senrui.mybatisplus.CoreMapper;
</#if>
/**
 * <p>
 * ${table.comment!} Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.mapperName} : ${superMapperClass}<${entity}>
<#else>
<#if (cfg.poXmlData['${entity}Po:poXmlDataFlag'])!false>
@Mapper
public interface ${table.mapperName} extends BaseMapper<${entity}, ${entity}Po> {
<#else>
@Mapper
public interface ${table.mapperName} extends CoreMapper<${entity}> {
</#if>
}
</#if>
