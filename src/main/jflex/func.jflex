package func;

import func.Token;

%%

%class JFlexLexer
%unicode
%line
%column
%type Token

%{
  public Token token(Token.Type type) {
    return new Token(type, yytext(), yycolumn, yyline+1, yylength());
  }
%}

identifier = [:jletter:][:jletterdigit:]*
digit      = [0-9]
integer    = {digit}+
whitespace = [ \t\n]

%%

":="            { return token(Token.Type.ASSIGN); }
"("             { return token(Token.Type.LPAR); }
")"             { return token(Token.Type.RPAR); }
";"             { return token(Token.Type.SEMI); }
","             { return token(Token.Type.COMMA); }

method          { return token(Token.Type.METHOD); }
vars            { return token(Token.Type.VARS); }
begin           { return token(Token.Type.BEGIN); }
return          { return token(Token.Type.RETURN); }
endmethod       { return token(Token.Type.ENDMETHOD); }
read            { return token(Token.Type.READ); }
write           { return token(Token.Type.WRITE); }
if              { return token(Token.Type.IF); }
then            { return token(Token.Type.THEN); }
else            { return token(Token.Type.ELSE); }
endif           { return token(Token.Type.ENDIF); }
while           { return token(Token.Type.WHILE); }
endwhile        { return token(Token.Type.ENDWHILE); }

less            { return token(Token.Type.LESS); }
lesseq          { return token(Token.Type.LESSEQ); }
eq              { return token(Token.Type.EQ); }
neq             { return token(Token.Type.NEQ); }

{whitespace}    { return token(Token.Type.WHITESPACE); }
{identifier}    { return token(Token.Type.IDENTIFIER); }
{integer}       { return token(Token.Type.INT_LITERAL); }

<<EOF>>         { return token(Token.Type.EOF); }
[^]            { return token(Token.Type.UNK); }