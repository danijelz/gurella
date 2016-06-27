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
test piece
@end

text after piece

@insertpiece (insertpiece)