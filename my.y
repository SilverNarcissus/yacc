%token
INTEGER
IDENTIFIER
+
-
*
/
(
)
##end_of_token
#identifier s means the operator stack
%CFG
S:E
{System.out.println("The result of this expression is:" + s.peek());}
#############
E:T
{}
#############
E:EAT
{int first = Integer.valueOf(s.pop()); String op = s.pop(); int second = Integer.valueOf(s.pop()); s.push(String.valueOf(op.equals("+")?(second + first):(second - first)));}
#############
T:F
{}
#############
T:TBF
{int first = Integer.valueOf(s.pop()); String op = s.pop(); int second = Integer.valueOf(s.pop()); s.push(String.valueOf(op.equals("*")?(second * first):(second / first)));}
#############
F:{(}E{)}
{s.pop(); String cur = s.pop(); s.pop(); s.push(cur);}
#############
F:{INTEGER}
{}
#############
F:{IDENTIFIER}
{}
#############
A:{+}
{}
#############
A:{-}
{}
#############
B:{*}
{}
#############
B:{/}
{}
#############
%end of CFG
