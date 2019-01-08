[![Build Status](https://travis-ci.org/rikcarve/mp-config-db.svg?branch=master)](https://travis-ci.org/rikcarve/mp-config-db)
[![codecov](https://codecov.io/gh/rikcarve/mp-config-db/branch/master/graph/badge.svg)](https://codecov.io/gh/rikcarve/mp-config-db)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.carve/mp-config-db/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/ch.carve/mp-config-db/)

# mp-config-db
A eclipse microprofile config (1.2) extension which uses [Consul](https://www.consul.io/) as source.

## Overview
The eclipse microprofile config framework is a simple yet powerful configuration framework for Java EE. But most implementations only provide the system/env properties or property files as configuration source. This small library provides an ConfigSource implementation which reads the values from the default datasource. For performance reasons, the config values are cached.

## Add dependency
```xml
        <dependency>
            <groupId>ch.carve</groupId>
            <artifactId>mp-config-db</artifactId>
            <version>0.3</version>
        </dependency>
```

## Configuration
Currently there are 2 values you can configure, either through Java system properties or environment variables:
* **mp-config-db.table** table name for configuration records, default value is "configurations"
* **mp-config-db.validity** how long to cache values (in seconds), default is 30s


## Links
* https://microprofile.io/project/eclipse/microprofile-config
* https://github.com/rikcarve/consulkv-maven-plugin
* https://github.com/rikcarve/mp-config-consul
