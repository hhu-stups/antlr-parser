package de.prob.parser.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import de.prob.parser.ast.SourceCodePosition;

public class Util {
	public static SourceCodePosition createSourceCodePosition(ParserRuleContext ctx) {
		SourceCodePosition sourceCodePosition = new SourceCodePosition();
		sourceCodePosition.setText(ctx.getText());
		sourceCodePosition.setStartLine(ctx.getStart().getLine());
		sourceCodePosition.setStartColumn(ctx.getStart().getCharPositionInLine());
		return sourceCodePosition;
	}

	public static SourceCodePosition createSourceCodePosition(Token ctx) {
		SourceCodePosition sourceCodePosition = new SourceCodePosition();
		sourceCodePosition.setText(ctx.getText());
		sourceCodePosition.setStartLine(ctx.getLine());
		sourceCodePosition.setStartColumn(ctx.getCharPositionInLine());
		return sourceCodePosition;
	}

}
