# 🛠️ JAI-VIER Backend Installation Guide

## 📦 Prerequisites

-   Make sure you have [**Maven**](https://maven.apache.org/) installed.
-   Clone the project and navigate to the backend folder:

```bash
cd MtdrSpring/backend/
```

## ⚙️ Install Dependencies

Run the following command to install all Maven dependencies:

```bash
mvn clean install
```

---

## 📝 Local Configuration (Do Not Push These Changes)

You'll need to manually update **three local files** for the app to run on your machine.

> ⚠️ **Important:** Never commit these changes to the `main` branch.

---

### 1. `config/OracleConfiguration.java`

Replace the `dataSource()` function with:

```java
public DataSource dataSource() throws SQLException {
    OracleDataSource ds = new OracleDataSource();

    // Local testing configuration
    ds.setDriverType(dbSettings.getDriver_class_name());
    logger.info("Using Driver " + dbSettings.getDriver_class_name());

    ds.setURL(dbSettings.getUrl());
    logger.info("Using URL: " + dbSettings.getUrl());

    ds.setUser(dbSettings.getUsername());
    logger.info("Using Username: " + dbSettings.getUsername());

    ds.setPassword(dbSettings.getPassword());

    return ds;
}
```

---

### 2. `security/WebSecurityConfiguration.java`

Replace the `configure()` function with:

```java
@Override
protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(csrf -> csrf.disable())
                .authorizeRequests(requests ->
                    requests.antMatchers("/**").permitAll()
                );
}
```

---

### 3. `src/main/resources/application.properties`

Update your `application.properties` file like so:

```properties
# --- Local Database Connection (not used in Kubernetes) ---
spring.datasource.url=<wallet high member>?TNS_ADMIN=path/to/your/wallet
spring.datasource.username=TODOUSER
spring.datasource.password=<your_wallet_password>

spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.type=oracle.ucp.jdbc.PoolDataSource
spring.datasource.oracleucp.connection-factory-class-name=oracle.jdbc.pool.OracleDataSource
spring.datasource.oracleucp.sql-for-validate-connection=select * from dual
spring.datasource.oracleucp.connection-pool-name=connectionPoolName1
spring.datasource.oracleucp.initial-pool-size=15
spring.datasource.oracleucp.min-pool-size=10
spring.datasource.oracleucp.max-pool-size=30

# --- Logging ---
logging.level.root=INFO
logging.level.com.springboot.MyTodoList=INFO
debug=true
# logging.level.oracle.ucp=trace

# --- Telegram Bot ---
telegram.bot.token=<your_bot_token>
telegram.bot.name=JAIVIERbot

# --- Server ---
server.port=8081
```

> 📁 Be sure to place your **wallet folder** inside the `src/main/resources/` directory.

---

## 🚀 Running the Project

### Start the application:

```bash
mvn spring-boot:run
```

### Run with debugging:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--debug
```
