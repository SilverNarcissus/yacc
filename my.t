12 9
s5 e0 e0 s4 e0 e0 g1 g2 g3
e0 s6 e0 e0 e0 a0 e0 e0 e0
e0 r2 s7 e0 r2 r2 e0 e0 e0
e0 r4 r4 e0 r4 r4 e0 e0 e0
s5 e0 e0 s4 e0 e0 g8 g2 g3
e0 r6 r6 r0 r6 r6 e0 e0 e0
s5 e0 e0 s4 e0 e0 e0 g9 g3
s5 e0 e0 s4 e0 e0 e0 e0 g10
e0 s6 e0 e0 s11 e0 e0 e0 e0
e0 r1 s7 e0 r1 r1 e0 e0 e0
e0 r3 r3 e0 r3 r3 e0 e0 e0
e0 r5 r5 e0 r5 r5 e0 e0 e0
#symbol_list
INTEGER
+
*
(
)
$
E
T
F
#productions
S:E
E:E+T
E:T
T:T*F
T:F
F:(E)
F:{INTEGER}