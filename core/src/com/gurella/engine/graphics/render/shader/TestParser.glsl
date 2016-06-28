@include (123)   
@include (223)

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

@piece
	piece text 1 //comment in piece 1
	@insertpiece (innerInsertpiece)
	@insertpiece (innerInsertpiece2)
	
	piece text 2 //comment in piece2
	//comment in piece 3
	
	@ifdef (abc)
		ifdef text in piece //comment in ifdef
	@end
	
	piece text 3
	//comment in piece 4
@end

text after piece

@insertpiece (insertpiece)

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