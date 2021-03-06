[![Build Status](https://travis-ci.org/rikcarve/mp-config-db.svg?branch=master)](https://travis-ci.org/rikcarve/mp-config-db)
[![codecov](https://codecov.io/gh/rikcarve/mp-config-db/branch/master/graph/badge.svg)](https://codecov.io/gh/rikcarve/mp-config-db)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.carve/mp-config-db/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/ch.carve/mp-config-db/)

# mp-config-db
A eclipse microprofile config extension which uses a database as source.

## Versions
0.7 -> Microprofile config 1.4 \
0.8 -> Microprofile config 2.0

## Overview
The eclipse microprofile config framework is a simple yet powerful configuration framework for Java EE. But most implementations only provide the system/env properties or property files as configuration source. This small library provides an ConfigSource implementation which reads the values from the default datasource. For performance reasons, the config values are cached.

## Add dependency
```xml
        <dependency>
            <groupId>ch.carve</groupId>
            <artifactId>mp-config-db</artifactId>
            <version>0.8</version>
        </dependency>
```

## Configuration
Currently there are 5 values you can configure, either through Java system properties or environment variables:
* **configsource.db.datasource** override default datasource by setting JNDI name of the datasource
* **configsource.db.table** table name for configuration records, default value is "configuration"
* **configsource.db.key-column** name of the column containing the key, default value is "key"
* **configsource.db.value-column** name of the column containing the value, default value is "value"
* **configsource.db.validity** how long to cache values (in seconds), default is 30s

## Ordinal
Config sources have priorities called ordinal. This config source has ordinal 450, but can be overriden with setting '**configsource.db.ordinal**' in one of the default config sources (NOT in this source like specified in the spec, as the datasource is not yet ready when getOrdinal() is called).

## Hint
Use memory config source from [microprofile-extensions](https://github.com/microprofile-extensions/config-ext/tree/master/configsource-memory) to get a REST interface and the possibility to change values on the fly (in-memory)

## Links
* https://microprofile.io/project/eclipse/microprofile-config
* https://github.com/rikcarve/consulkv-maven-plugin
* https://github.com/rikcarve/mp-config-consul
* https://github.com/microprofile-extensions

