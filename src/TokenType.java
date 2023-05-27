
public enum TokenType {
    Const, Void, Else, If, Int, While, Return,
    Char, Double, For, Do, Goto, Switch, Case, Break, Default,  //1. 추가 키워드
    Eof,
    LeftBrace, RightBrace, LeftBracket, RightBracket,
    LeftParen, RightParen, Semicolon, Comma, Assign, AddAssign, SubAssign, MultAssign, DivAssign, RemAssign,
    Equals, Less, LessEqual, Greater, GreaterEqual,
    Not, NotEqual, Plus, Minus, Multiply, Reminder,
    Increment, Decrement,
    Colon,                                                      //2. 추가 연산자: ':'

    Divide, And, Or, Identifier, IntLiteral,
    charLiteral,stringLiteral,doubleLiteral,                    //3. 추가 인식 리터럴

}
