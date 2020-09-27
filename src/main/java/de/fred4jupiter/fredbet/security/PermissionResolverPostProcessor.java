package de.fred4jupiter.fredbet.security;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class PermissionResolverPostProcessor implements BeanPostProcessor {

    private final PermissionResolver permissionResolver;

    public PermissionResolverPostProcessor(PermissionResolver permissionResolver) {
        this.permissionResolver = permissionResolver;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof UserDetailsService) {
            return new UserDetailsServicePermissionDecorator((UserDetailsService) bean, permissionResolver);
        }
        return bean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

}
