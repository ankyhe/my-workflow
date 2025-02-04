package com.gmail.at.ankyhe.wdl.parser;

import javax.validation.constraints.NotBlank;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import com.gmail.at.ankyhe.wdl.parser.listener.MyWdlParserListener;
import com.gmail.at.ankyhe.wdl.parser.model.WdlDocument;

public class SimpleWdlParser {

    public WdlDocument parse(@NotBlank final String s) {
        final WdlLexer lexer = new WdlLexer(CharStreams.fromString(s));
        final WdlParser parser = new WdlParser(new CommonTokenStream(lexer));
        final ParseTree tree = parser.document();
        final MyWdlParserListener listener = new MyWdlParserListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);

        return WdlDocument.builder().version(listener.getVersion()).tasks(listener.getTasks()).workflows(listener.getWorkflows()).build();
    }
}
