# EduDB

The Open Source Educational Database Engine

## Build

### prerequisites

install required jar files in your local maven repository

```bash
./install-jars.sh
```

for macOS and Linux

```bash
./mvnw clean package
```

for Windows

```bash
mvnw.cmd clean package
```

## Docs

### generating the docs

for macOS and Linux

```bash
./mvnw javadoc:javadoc
```

for Windows

```bash
mvnw.cmd javadoc:javadoc
```

### viewing the docs

open the [docs](target/site/apidocs/index.html) from `target/site/apidocs/index.html` in your browser
