dataSource {
    pooled = true
    jmxExport = true
    driverClassName = "com.mysql.jdbc.Driver"
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/SpringSecurity?useUnicode=yes&characterEncoding=UTF-8&useSSL=false"
            username = "root"
            password = "12345"
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/SpringSecurity?useUnicode=yes&characterEncoding=UTF-8&useSSL=false"
            username = "root"
            password = "12345"
        }
    }
    production {
        dataSource {
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/SpringSecurity?useUnicode=yes&characterEncoding=UTF-8&useSSL=false"
            username = "root"
            password = "12345"
        }
    }
}