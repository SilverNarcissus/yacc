%token
INTEGER
+
*
(
)
##end_of_token
%CFG
S:E
E:T
E:E{+}T
T:F
T:T{*}F
F:{(}E{)}
F:{INTEGER}
##end of CFG