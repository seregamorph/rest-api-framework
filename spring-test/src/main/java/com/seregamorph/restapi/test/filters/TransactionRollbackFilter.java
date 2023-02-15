package com.seregamorph.restapi.test.filters;

import static org.springframework.test.context.transaction.TestContextTransactionUtils.DEFAULT_TRANSACTION_MANAGER_NAME;
import static org.springframework.util.Assert.state;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that adds MockMvc test behaviour:
 * Transaction always Rollback policy
 *
 * @see com.seregamorph.restapi.test.config.TransactionRollbackFilterBootConfig
 */
@Slf4j
@RequiredArgsConstructor
public class TransactionRollbackFilter extends OncePerRequestFilter {

    private final ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(),
                "Current thread is in transaction already");

        val transactionManager = retrieveTransactionManager();

        val transactionDefinition = new RuleBasedTransactionAttribute();
        val transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            filterChain.doFilter(request, response);
        } finally {
            transactionManager.rollback(transactionStatus);
        }
    }

    /**
     * {@link org.springframework.test.context.transaction.TestContextTransactionUtils#retrieveTransactionManager(
     * org.springframework.test.context.TestContext, String)}
     */
    private PlatformTransactionManager retrieveTransactionManager() {
        BeanFactory bf = applicationContext.getAutowireCapableBeanFactory();

        if (bf instanceof ListableBeanFactory) {
            ListableBeanFactory lbf = (ListableBeanFactory) bf;

            val txMgrs = BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, PlatformTransactionManager.class);
            if (txMgrs.size() == 1) {
                return txMgrs.values().iterator().next();
            }

            try {
                return bf.getBean(PlatformTransactionManager.class);
            } catch (BeansException ex) {
                log.debug("Could not get bean", ex);
            }

            val configurers = BeanFactoryUtils.beansOfTypeIncludingAncestors(lbf, TransactionManagementConfigurer.class);
            state(configurers.size() <= 1, "Only one TransactionManagementConfigurer may exist in the ApplicationContext");
            if (configurers.size() == 1) {
                return configurers.values().iterator().next().annotationDrivenTransactionManager();
            }
        }

        return bf.getBean(DEFAULT_TRANSACTION_MANAGER_NAME, PlatformTransactionManager.class);
    }

}
