package ke.co.esuite.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
@MapperScan(basePackages = "ke.co.esuite.db.persistence", annotationClass = UseDatasourceDb.class, sqlSessionFactoryRef ="DbSessionFactory")
public class DataConfig {
	
	private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
	
	private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
	private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
	private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
	
	@Autowired
	private Environment env;
	
	@Bean(name = "db")
	@Primary
    public BasicDataSource dataSource() {
		
		final BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
		ds.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
		ds.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
		ds.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
		ds.setInitialSize(5);
		return ds;
    }

    @Bean
    @Primary
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = "DbSessionFactory")
    @Primary
    public SqlSessionFactoryBean sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setTypeAliasesPackage("ke.co.esuite.db.persistence.domain");
        return sessionFactory;
    }
}
