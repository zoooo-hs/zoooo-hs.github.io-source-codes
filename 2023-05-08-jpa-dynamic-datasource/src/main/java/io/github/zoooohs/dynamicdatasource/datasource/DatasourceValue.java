package io.github.zoooohs.dynamicdatasource.datasource;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class DatasourceValue {
    private String url;
    private String driverClassName;
    private String username;
    private String password;
}
