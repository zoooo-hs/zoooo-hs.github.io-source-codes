# JPA 동적 DataSource를 Runtime에 적용해보자

[블로그 게시글](https://zoooo-hs.github.io/2022/06/28/JPA-동적-DataSource를-Runtime에-적용해보자.html)
## 준비물
- mariadb 1
  - localhost:3306
  - database name: db1
  - root password: mariadb
    - 혹은 application.properties에서 알맞은 값으로 변경
  - scheme
```sql
-- db1.dog definition
CREATE TABLE `dog` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` varchar(100) DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;
  ```
- mariadb 2
  - 다른 url, 같은 스키마 사용
## 실행 방법
  ```bash
./gradlew bootRun
  ```

## Future Works
- EntityManagerFactory Singleton per Datasource
