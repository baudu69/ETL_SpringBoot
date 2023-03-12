# ETL Spring boot
Cet ETL permet de récupérer les données d'une base de données et de les insérer dans une autre base de données.

## Lancement
Pour lancer l'application, il faut lancer le docker-compose.yml pour lancer les bases de données de démonstration.

SpringDoc est utilisé pour la documentation de l'API. Pour accéder à la documentation, il faut se rendre à l'adresse suivante : http://localhost:8080/swagger-ui.html

## Ajouter une base de données
On peut ajouter une base de données en ajoutant les propriétés suivantes dans le fichier application.properties :
```properties
spring.datasource.{datasource-name}.url=jdbc:{db-type}://{db-url}/{db-name}
spring.datasource.{datasource-name}.username={datasource-username}
spring.datasource.{datasource-name}.password={datasource-password}
spring.datasource.{datasource-name}.driver-class-name={datasource-driver-name}
```
Ensuite, on lui créé un ficher de configuration :
```java
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class {datasource-name}DatasourceConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.{datasource-name}")
	public DataSourceProperties {datasource-name}DataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource {datasource-name}DataSource() {
		return {datasource-name}DataSourceProperties().initializeDataSourceBuilder().build();
	}
}
```
On ajoute ensuite un bean de JdbcTemplate dans le fichier de configuration de l'application [DBConfig.java](./src/main/java/com/example/testetl/configuration/DBConfig.java)

```java
@Bean
	public JdbcTemplate {datasource-name}JdbcTemplate(@Qualifier("{datasource-name}DataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
```

Enfin, on l'ajoute dans le fichier de configuration du selecteur de base de données [DBSelector.java](./src/main/java/com/example/testetl/configuration/DBSelector.java):
```java
@Service
public class DBSelector {
	private final JdbcTemplate mySqlJdbcTemplate;
	private final JdbcTemplate postGreJdbcTemplate;
	private final JdbcTemplate {datasource-name}JdbcTemplate;

	public DBSelector(
			@Qualifier("mySqlJdbcTemplate") JdbcTemplate mySqlJdbcTemplate,
			@Qualifier("postGreJdbcTemplate") JdbcTemplate postGreJdbcTemplate
			@Qualifier("{datasource-name}JdbcTemplate") JdbcTemplate {datasource-name}JdbcTemplate
	) {
		this.mySqlJdbcTemplate = mySqlJdbcTemplate;
		this.postGreJdbcTemplate = postGreJdbcTemplate;
		this.{datasource-name}JdbcTemplate = {datasource-name}JdbcTemplate;
	}

	public JdbcTemplate getJdbcTemplate(String db) {
		return switch (db) {
			case "mysql" -> mySqlJdbcTemplate;
			case "postgre" -> postGreJdbcTemplate;
			case "{datasource-name}" -> {datasource-name}JdbcTemplate;
			default -> null;
		};
	}
}