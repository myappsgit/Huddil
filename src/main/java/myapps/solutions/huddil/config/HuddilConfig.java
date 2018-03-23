package myapps.solutions.huddil.config;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.persistence.ValidationMode;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableTransactionManagement
@EnableSwagger2
@PropertySource(value = { "classpath:application.properties" })
public class HuddilConfig {

	@Autowired
	private Environment environment;

	@Bean(name = "huddilEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean huddilEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(huddilDataSource());
		entityManager.setPackagesToScan(new String[] { "myapps.solutions.huddil.model" });
		entityManager.setPersistenceUnitName("huddil");
		entityManager.setValidationMode(ValidationMode.AUTO);

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManager.setJpaVendorAdapter(vendorAdapter);
		entityManager.setJpaProperties(huddilHibernateProperties());
		return entityManager;
	}

	@Bean
	public DataSource huddilDataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
		} catch (IllegalStateException | PropertyVetoException e) {
			e.printStackTrace();
		}
		dataSource.setJdbcUrl(environment.getRequiredProperty("huddil.jdbc.url"));
		dataSource.setUser(environment.getRequiredProperty("huddil.jdbc.username"));
		dataSource.setPassword(environment.getRequiredProperty("huddil.jdbc.password"));
		dataSource.setMinPoolSize(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.max_size")));
		dataSource.setMaxPoolSize(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.min_size")));
		dataSource.setMaxIdleTime(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.idle_test_period")));
		dataSource.setMaxStatements(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.max_statements")));
		return dataSource;
	}

	private Properties huddilHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
		properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
		properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
		return properties;
	}

	@Bean(name = "huddilTranscationManager")
	public PlatformTransactionManager huddilTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(huddilEntityManagerFactory().getObject());
		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor huddilExceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setDefaultEncoding("utf-8");
		commonsMultipartResolver.setMaxUploadSize(50000000);
		return commonsMultipartResolver;
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages_locale");
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build();
	}

	@Bean(name = "wrapperEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean wrapperEntityManagerFactory() {
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(dataSource());
		entityManager.setPackagesToScan(new String[] { "myapps.solutions.huddil.model" });
		entityManager.setPersistenceUnitName("wrapper");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManager.setJpaVendorAdapter(vendorAdapter);
		entityManager.setJpaProperties(huddilHibernateProperties());
		return entityManager;
	}

	@Bean
	public DataSource dataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(environment.getRequiredProperty("jdbc.driverClassName"));
		} catch (IllegalStateException | PropertyVetoException e) {
			e.printStackTrace();
		}
		dataSource.setJdbcUrl(environment.getRequiredProperty("wrapper.jdbc.url"));
		dataSource.setUser(environment.getRequiredProperty("wrapper.jdbc.username"));
		dataSource.setPassword(environment.getRequiredProperty("wrapper.jdbc.password"));
		dataSource.setMinPoolSize(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.max_size")));
		dataSource.setMaxPoolSize(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.min_size")));
		dataSource.setMaxIdleTime(Integer.parseInt(environment.getRequiredProperty("hibernate.c3p0.idle_test_period")));
		return dataSource;
	}

	@Bean(name = "wrapperTranscationManager")
	public PlatformTransactionManager wrapperTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(wrapperEntityManagerFactory().getObject());
		return transactionManager;
	}
}
