# hql-validator

## Overview
Cli validator of HiveQl scripts

## Usage

```bash
./gradlew shadowJar
java -jar build/libs/hql-validator-1.0-SNAPSHOT.jar <paths>
```

`<paths>` - list of paths to the hive scripts (.sql). Path can be directory or file.

If nothing happens when you execute the command, then there are no errors in the scripts, otherwise there will be logs with specific errors.
