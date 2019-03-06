package io.github.atkawa7.httpsnippet.builder;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CodeBuilder {
// empty characters
public static final String EOL = "\n";
public static final String TAB = "\t";
public static final String EMPTY = "";
public static final String SPACE = "  ";

public static final String HTTPS = "https";

private List<String> code;
private String indentation;
private String lineJoin;

public CodeBuilder() {
	this(TAB);
}

public CodeBuilder(String indentation) {
	this(indentation, EOL);
}

public CodeBuilder(String indentation, String lineJoin) {
	this.indentation = indentation;
	this.lineJoin = lineJoin;
	this.code = new ArrayList<>();
}

public StringBuilder fill(int indentationLevel) {
	StringBuilder stringBuilder = new StringBuilder();
	stringBuilder.append(EMPTY);
	while (indentationLevel > 0) {
	stringBuilder.append(this.indentation);
	indentationLevel--;
	}
	return stringBuilder;
}

public String buildLine(String line) {
	StringBuilder stringBuilder = this.fill(0);
	stringBuilder.append(line);
	return line;
}

public String buildLine(String line, String... args) {
	StringBuilder stringBuilder = this.fill(0);
	stringBuilder.append(line);
	return String.format(stringBuilder.toString(), args);
}

public String buildLine(int indentationLevel, String line) {
	StringBuilder stringBuilder = this.fill(indentationLevel);
	stringBuilder.append(line);
	return stringBuilder.toString();
}

public String buildLine(int indentationLevel, String line, String... args) {
	StringBuilder stringBuilder = this.fill(indentationLevel);
	stringBuilder.append(line);
	return String.format(stringBuilder.toString(), args);
}

public CodeBuilder push(String line) {
	this.code.add(buildLine(line));
	return this;
}

public CodeBuilder push(String line, String... args) {
	this.code.add(buildLine(line, args));
	return this;
}

public CodeBuilder push(int indentationLevel, String line) {
	this.code.add(buildLine(indentationLevel, line));
	return this;
}

public CodeBuilder push(int indentationLevel, String line, String... args) {
	this.code.add(buildLine(indentationLevel, line, args));
	return this;
}

public CodeBuilder blank() {
	this.code.add(EMPTY);
	return this;
}

public String join(String lineJoin) {
	return String.join(lineJoin, this.code);
}

public String join() {
	return this.join(this.lineJoin);
}
}
