%token
INTEGER
IDENTIFIER
##end of token

%CFG
S:E
E:T
E:EAT
T:F
T:TMF
F:(E)
F:{INTEGER}
F:{IDENTIFIER}
A:+
A:-
M:*
M:/
##end of CFG