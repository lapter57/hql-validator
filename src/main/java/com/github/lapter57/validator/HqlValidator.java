package com.github.lapter57.validator;

import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.apache.hadoop.hive.ql.parse.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HqlValidator {

    private static final Logger log = LoggerFactory.getLogger(HqlValidator.class);

    public void validate(final List<Path> paths) {
        final List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Path path : paths) {
            futures.add(CompletableFuture.runAsync(() -> validate(path)));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[paths.size()])).join();
    }

    public void validate(final Path path) {
        try {
            final List<Path> sqlPaths = Files.isDirectory(path)
                    ? getSqlPathsFromDir(path)
                    : Collections.singletonList(path);
            for (Path sqlPath : sqlPaths) {
                validateSqlFile(sqlPath);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void validateSqlFile(final Path path) throws IOException {
        try (final Stream<String> lines = Files.lines(path)) {
            final String data = lines.map(line -> line.split("--")[0])
                    .filter(line -> !line.trim().isEmpty())
                    .collect(Collectors.joining("\n"));
            final String[] queries = data.split(";");
            for (final String query : queries) {
                try {
                    validateQuery(query);
                } catch (ParseException e) {
                    log.error("{} . Error in {} ; query = \n {} \n", e.getMessage(), path, query);
                }
            }
        }
    }

    private static List<Path> getSqlPathsFromDir(final Path dirPath) throws IOException {
        try (Stream<Path> walk = Files.walk(dirPath)) {
            return walk
                    .filter(p -> !Files.isDirectory(p))
                    .map(Path::toString)
                    .filter(f -> f.endsWith("sql"))
                    .map(Paths::get)
                    .collect(Collectors.toList());
        }
    }

    private static void validateQuery(final String query) throws ParseException {
        if (!query.isEmpty()) {
            new ParseDriver().parse(query);
        }
    }
}
