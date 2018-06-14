package pl.invoice.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties
@Configuration
public class DbPropertiesConfig {

  private String filePath;
  private String multiFileRootFolder;
  private String database;
  private String sqlDatabase;
  private String sqlDbUser;
  private String sqlDbPassword;

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getMultiFileRootFolder() {
    return multiFileRootFolder;
  }

  public void setMultiFileRootFolder(String multiFileRootFolder) {
    this.multiFileRootFolder = multiFileRootFolder;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getSqlDatabase() {
    return sqlDatabase;
  }

  public void setSqlDatabase(String sqlDatabase) {
    this.sqlDatabase = sqlDatabase;
  }

  public String getSqlDbUser() {
    return sqlDbUser;
  }

  public void setSqlDbUser(String sqlDbUser) {
    this.sqlDbUser = sqlDbUser;
  }

  public String getSqlDbPassword() {
    return sqlDbPassword;
  }

  public void setSqlDbPassword(String sqlDbPassword) {
    this.sqlDbPassword = sqlDbPassword;
  }
}
