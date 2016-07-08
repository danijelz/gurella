@include (123)
@include (223)

@for (2, n) @skip
inSkippedForLoop + @value(n) @skip
@end

text
text

//comment @include (123)
text after comment

/*
block @include (123)
comment 0
*/text after block comment 0

/*
block @include (123)
comment 1
*/
text after block comment 1

text after 
block comments

@piece(innerInsertpiece)innerInsertpieceText@end
@piece(innerInsertpiece2)innerInsertpiece2Text@end
@piece(ifdefInsertpiece)ifdefInsertpieceText@end

@piece(piece1name)
	piece text 1 //comment in piece 1
	@insertpiece (innerInsertpiece)
	@insertpiece (innerInsertpiece2)

	piece text 2 //comment in piece2
	//comment in piece 3

	@ifdef (abc)    ifdef text in piece //comment in ifdef
@end

	piece text 3
	//comment in piece 4
@end

text after piece

@insertpiece (piece1name)

text after insertpiece
//comment after insertpiece

@ifdef ((abc | acc) & (bbc | ccc))
	ifdef text
	@insertpiece (ifdefInsertpiece)
@end

bbbbbbbbbbbbbb
bbbbbbbb

bb
bbbbbbbbbbbb

@ifdef ((abc | acc) & (bbc | ccc))
	ifdef text
	@insertpiece (ifdefInsertpiece)
	@ifdef ((abc | acc) & (bbc | ccc))
		ifdef text
		@insertpiece (ifdefInsertpiece)
	@end
@end


dddaaannn

@ifdef ((abc | acc) & (bbca | ccc))
	ifdef(abc | acc) & (bbc | ccc)
	@insertpiece (ifdefInsertpiece)
@end

@for (2, n)
inForLoop@end

@for (dddVar, n)
inDddVarForLoop@end

@set (testVar, 2)
@for (testVar, n)
set + @value(n)@end

@add (testVar, 2)
@for (testVar, n)
add + @value(n)@end

@sub (testVar, 1)
@for (testVar, n)
sub + @value(n)@end

@mul (testVar, 2)
@for (testVar, n)
mul + @value(n)@end

@div (testVar, 2)
@for (testVar, n)
div + @value(n)@end

@mod (testVar, 2)
@for (testVar, n)
mod + @value(n)@end

@for (testVar, n)
inTestVarForLoop@value(testVar) + @value(n)@end

pre add variable: @value(testVar), @add (testVar, dddVar)post: @value(testVar) 

min: @min(testVar, -1) @value(testVar)

@ifexp(testVar, -1)ifexp(testVar, -1)@end
@ifexp(testVar, -1, ==)ifexp(testVar, -1, ==)@end
@ifexp(testVar, 1, !=)ifexp(testVar, 1, !=)@end
@ifexp(testVar, -10, >)ifexp(testVar, -10, >)@end
@ifexp(testVar, 10, <)ifexp(testVar, 10, <)@end
@ifexp(testVar, -10, >=)ifexp(testVar, -10, >=)@end
@ifexp(testVar, 10, <=)ifexp(testVar, 10, <=)@end

max: @max(testVar, 100) @value(testVar)
// @skip
dd

@for (2, n) @skip
inSkippedForLoop + @value(n)@skip
@end
