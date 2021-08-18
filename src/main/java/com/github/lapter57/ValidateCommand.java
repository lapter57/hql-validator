package com.github.lapter57;

import com.github.lapter57.validator.HqlValidator;
import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;

import javax.inject.Inject;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "validate", description = "Validate Hive scripts")
public class ValidateCommand {

    @Inject
    private HelpOption<ValidateCommand> help;

    @Arguments(
            title = "paths",
            description = "List of paths to the hive scripts (.sql). Path can be directory or file")
    private List<String> pathStrings = new ArrayList<>();

    public static void main(String[] args) {
        SingleCommand<ValidateCommand> parser = SingleCommand.singleCommand(ValidateCommand.class);
        ValidateCommand cmd = parser.parse(args);
        cmd.run();
    }

    private void run() {
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {}
        }));

        if (!help.showHelpIfRequested()) {
            final HqlValidator validator = new HqlValidator();
            List<Path> paths = pathStrings.stream()
                    .map(Paths::get)
                    .collect(Collectors.toList());
            validator.validate(paths);
        }
    }
}
