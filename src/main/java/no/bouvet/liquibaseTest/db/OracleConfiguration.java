package no.bouvet.liquibaseTest.db;

import liquibase.integration.spring.MultiTenantSpringLiquibase;
import liquibase.integration.spring.SpringLiquibase;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class OracleConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSource dataSource() throws SQLException {

        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.liquibase-datasource")
    public DataSource liquibaseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.primary-liquibase.liquibase")
    public LiquibaseProperties primaryLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean("liquibase")
    public SpringLiquibase primaryLiquibase() {
        return springLiquibase(liquibaseDataSource(), primaryLiquibaseProperties());
    }

    @Bean
    @DependsOn("liquibase") // ensure execution after SpringLiquibase Bean
    public MultiTenantSpringLiquibase liquibaseMt(DataSource dataSource, LiquibaseProperties liquibaseProperties) {

        MultiSchemaLiquibase liquibase = new MultiSchemaLiquibase();

        liquibase.setDataSource(liquibaseDataSource());
        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
        liquibase.setSchemas(Arrays.asList("EVENT_DATA_SE1", "EVENT_DATA_DK1","EVENT_DATA_NO1"));
        return liquibase;
    }

    private static SpringLiquibase springLiquibase(DataSource dataSource, LiquibaseProperties properties) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setContexts(properties.getContexts());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setLabels(properties.getLabels());
        liquibase.setChangeLogParameters(properties.getParameters());
        liquibase.setRollbackFile(properties.getRollbackFile());
        return liquibase;
    }
}
