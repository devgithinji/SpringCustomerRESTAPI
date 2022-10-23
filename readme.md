##### Spring 5 and Hibernate 5 ORM framework integration using Java Based configuration only(no XML)

To integrate Hibernate with Spring MVC application, you can use the *LocalSessionFactoryBean* class, which set up a shared *SessionFactory* object within a Spring application context. This *SessionFactory* object can be passed to DAO classes via dependencies injection.

##### Short note on how Spring supports Hibernate Integration

Basically, in order to support Hibernate integration, Spring provides two key beans available in the *org.springframework.orm.hibernate5* package:

1. LocalSessionFactoryBean: creates a Hibernate’s SessionFactory which is injected into Hibernate-based DAO classes.
2. HibernateTransactionManager: provides transaction support code for a SessionFactory. Programmers can use @Transactional annotation in DAO methods to avoid writing boiler-plate transaction code explicitly.

##### Development Steps

1. Create Maven Web Application
2. Add Dependencies - pom.xml File

   ```
   <!-- Spring MVC Dependency -->
           <dependency>
               <groupId>org.springframework</groupId>
               <artifactId>spring-webmvc</artifactId>
               <version>${spring.version}</version>
           </dependency>
           <!-- Spring ORM -->
           <dependency>
               <groupId>org.springframework</groupId>
               <artifactId>spring-orm</artifactId>
               <version>${spring.version}</version>
           </dependency>
           <!-- Hibernate Core -->
           <dependency>
               <groupId>org.hibernate</groupId>
               <artifactId>hibernate-core</artifactId>
               <version>5.2.17.Final</version>
           </dependency>
           <!-- Hibernate Validator -->
           <dependency>
               <groupId>org.hibernate</groupId>
               <artifactId>hibernate-validator</artifactId>
               <version>${hibernate.validator}</version>
           </dependency>
           <!-- JSTL Dependency (for JSP tags)-->
           <dependency>
               <groupId>javax.servlet.jsp.jstl</groupId>
               <artifactId>javax.servlet.jsp.jstl-api</artifactId>
               <version>${jstl.version}</version>
           </dependency>
           <dependency>
               <groupId>taglibs</groupId>
               <artifactId>standard</artifactId>
               <version>${tld.version}</version>
           </dependency>
           <!-- Servlet Dependency -->
           <dependency>
               <groupId>javax.servlet</groupId>
               <artifactId>javax.servlet-api</artifactId>
               <version>${servlets.version}</version>
               <scope>provided</scope>
           </dependency>
           <!-- JSP Dependency (to surpport JSP)-->
           <dependency>
               <groupId>javax.servlet.jsp</groupId>
               <artifactId>javax.servlet.jsp-api</artifactId>
               <version>${jsp.version}</version>
               <scope>provided</scope>
           </dependency>
          <!-- MySQL Dependency -->
           <dependency>
               <groupId>mysql</groupId>
               <artifactId>mysql-connector-java</artifactId>
               <version>5.1.47</version>
           </dependency>
   ```
3. Project Structure
5. AppInitializer - Register a DispatcherServlet using Java-based Spring configuration

   ```
   package com.densoft.springcusomerscrm.config;

   import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

   public class DispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
       @Override
       protected Class<?>[] getRootConfigClasses() {
           return new Class[0];
       }

       @Override
       protected Class<?>[] getServletConfigClasses() {
           return new Class[]{AppConfig.class};
       }

       @Override
       protected String[] getServletMappings() {
           return new String[]{"/"};
       }
   }

   ```
6. PersistenceJPAConfig - Spring and Hibernate Integration using Java-based Spring configuration

```
package com.densoft.springcusomerscrm.config;

import com.mchange.v2.c3p0.DriverManagerDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableWebMvc
@ComponentScan("com.densoft.springcusomerscrm")
@PropertySource({"classpath:persistence-mysql.properties"})
@EnableTransactionManagement
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private Environment env;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClass(env.getProperty("jdbc.driver"));
        dataSource.setJdbcUrl(env.getProperty("jdbc.url"));
        dataSource.setUser(env.getProperty("jdbc.user"));
        dataSource.setPassword(env.getProperty("jdbc.password"));

        return dataSource;
    }


    private Properties getHibernateProperties() {
        // set hibernate properties
        Properties properties = new Properties();

        properties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));

        return properties;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        // create session factory
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        // set the properties
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan(env.getProperty("hibernate.packagesToScan"));
        sessionFactory.setHibernateProperties(getHibernateProperties());

        return sessionFactory;
    }

    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        // setup transaction manager based on session factory
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

}

```

* *LocalSessionFactoryBean* creates a Hibernate  *SessionFactory* . This is the usual way to set up a shared Hibernate *SessionFactory* in a Spring application context.
* *EnableTransactionManagement* enables Spring’s annotation-driven transaction management capability.
* *HibernateTransactionManager* binds a Hibernate Session from the specified factory to the thread, potentially allowing for one thread-bound Session per factory. This transaction manager is appropriate for applications that use a single Hibernate SessionFactory for transactional data access, but it also supports direct DataSource access within a transaction i.e. plain JDBC.

### database.properties

Create database.properties file under the resources folder and put following database configuration in it.

```
#
# JDBC connection properties
#
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/customer_crm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
jdbc.user=dennis
jdbc.password=password

#
# Hibernate properties
#
hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
hibernate.show_sql=true
hibernate.packagesToScan=com.densoft.springcusomerscrm.model
```

8. WebMvcConfig - Spring MVC Bean Configuration using Java-based Spring configuration

   Create an MVCConfig class and annotated with  *@Configuration* ,  *@EnableWebMvc* , and *@ComponentScan* annotations.

   ```
   package com.densoft.springcusomerscrm.config;

   import com.mchange.v2.c3p0.DriverManagerDataSource;
   import org.hibernate.SessionFactory;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.ComponentScan;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.context.annotation.PropertySource;
   import org.springframework.core.env.Environment;
   import org.springframework.orm.hibernate5.HibernateTransactionManager;
   import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
   import org.springframework.transaction.annotation.EnableTransactionManagement;
   import org.springframework.web.servlet.config.annotation.EnableWebMvc;
   import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
   import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

   import javax.sql.DataSource;
   import java.util.Properties;

   @Configuration
   @EnableWebMvc
   @ComponentScan("com.densoft.springcusomerscrm")
   @PropertySource({"classpath:persistence-mysql.properties"})
   @EnableTransactionManagement
   public class AppConfig implements WebMvcConfigurer {

       @Override
       public void addResourceHandlers(ResourceHandlerRegistry registry) {
           registry.addResourceHandler("/resources/**")
                   .addResourceLocations("/resources/");
       }
   }

   ```
9. JPA Entity - Customer.java
10. Spring MVC Controller Class - CustomerController.java
11. Service Layer - CustomerService.java and CustomerServiceImpl.java
12. DAO Layer - CustomerDAO.java and CustomerDAOImpl.java 11 JSP Views - customer-form.jsp and list-customers.jsp
13. Serve Static Resources - CSS and JS

    1. Create a *resource* folder under webapp directory.
    2. Create *css* and *js* folders under the *resource* directory.
    3. Download and keep **bootstrap.min.css** file under *css* folder
    4. download and keep **bootstrap.min.js** and **jquery-1.11.1.min.js** files under the resource directory. Note that bootstrap min js is depended on jquery min js.
14. Build and Run an application

    As we are using maven build tool so first, we will need to build this application using following maven command:

    ```
    clean install
    ```
15. Demo
