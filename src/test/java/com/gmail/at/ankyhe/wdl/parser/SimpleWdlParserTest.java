package com.gmail.at.ankyhe.wdl.parser;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import com.gmail.at.ankyhe.wdl.parser.model.WdlDocument;
import org.springframework.util.ResourceUtils;

@Slf4j
public class SimpleWdlParserTest {

    @Test
    void testParse() {
        final String s = stringFromWdlFile("test1.wdl");
        final SimpleWdlParser simpleWdlParser = new SimpleWdlParser();
        final WdlDocument wdlDocument = simpleWdlParser.parse(s);

        log.info("wdlDocument is {}", wdlDocument);
    }

    private static String stringFromWdlFile(final String wdlFile) {
        try {
            File file = ResourceUtils.getFile("classpath:" + Paths.get("wdl", wdlFile));
            return Files.readString(file.toPath());
        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
