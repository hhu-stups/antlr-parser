package de.prob.parser.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import de.prob.parser.ast.SourceCodePosition;

final class Util {
	static SourceCodePosition createSourceCodePosition(ParserRuleContext ctx) {
		return new SourceCodePosition(
			ctx.getStart().getLine(),
			ctx.getStart().getCharPositionInLine(),
			ctx.getText()
		);
	}

	static SourceCodePosition createSourceCodePosition(Token ctx) {
		return new SourceCodePosition(
			ctx.getLine(),
			ctx.getCharPositionInLine(),
			ctx.getText()
		);
	}

}
