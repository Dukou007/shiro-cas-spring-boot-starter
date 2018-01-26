package org.apache.shiro.spring.boot;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.spring.boot.cache.ShiroEhCacheAutoConfiguration;
import org.apache.shiro.spring.boot.cas.CasSubjectFactory;
import org.apache.shiro.spring.config.web.autoconfigure.ShiroWebAutoConfiguration;
import org.apache.shiro.spring.web.config.AbstractShiroWebConfiguration;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.filter.authc.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter({ShiroEhCacheAutoConfiguration.class, ShiroPac4jCasWebAutoConfiguration.class})
@AutoConfigureBefore(value = { ShiroWebAutoConfiguration.class}, name = {"org.apache.shiro.spring.boot.ShiroBizWebAutoConfiguration.class"})
@ConditionalOnClass({AuthenticationFilter.class})
@ConditionalOnProperty(prefix = ShiroCasProperties.PREFIX, value = "enabled", havingValue = "true")
@EnableConfigurationProperties({ ShiroProperties.class })
public class ShiroCasWebAutoConfiguration extends AbstractShiroWebConfiguration {
	
	@Autowired
	private ShiroProperties properties;
	
	/**
	 * 责任链定义 ：定义Shiro的逻辑处理责任链
	 */
	@Bean
    @ConditionalOnMissingBean
    @Override
	protected ShiroFilterChainDefinition shiroFilterChainDefinition() {
		DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
		Map<String /* pattert */, String /* Chain names */> pathDefinitions = properties.getFilterChainDefinitionMap();
		if (MapUtils.isNotEmpty(pathDefinitions)) {
			chainDefinition.addPathDefinitions(pathDefinitions);
			return chainDefinition;
		}
		chainDefinition.addPathDefinition("/logout", "logout");
		chainDefinition.addPathDefinition("/**", "authc");
		return chainDefinition;
	}
	
	@Bean
	@ConditionalOnMissingBean
	@Override
    protected SubjectFactory subjectFactory() {
        return new CasSubjectFactory();
    }
	
}
