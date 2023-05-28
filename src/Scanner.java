import java.io.*;

public class Scanner {

    private boolean isEof = false;                                  // eof 체크용
    private char ch = ' ';
    private BufferedReader input;
    private String line = "";                                       //한 문자 ch ,입력버퍼 , 한 줄 line
    private int lineno = 0;
    private int col = 1;                                            //행 lineno,열 col
    private final String letters = "abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String digits = "0123456789";                     //letter N digit
    private final char eolnCh = '\n';
    private final char eofCh = '\004';                              // 줄바꿈,

    private String comment="";                                      //주석 안 내용 출력을 위한 변수
    private String file_name="";                                      //주석 안 내용 출력을 위한 변수
    public Scanner (String fileName) { // source filename           //파일을 불러온다
        System.out.println("Begin scanning... programs/" + fileName + "\n");
        try {
            input = new BufferedReader (new FileReader(fileName));
            file_name=fileName;
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
            System.exit(1);
        }
    }

    private char nextChar() {                                       //다음 문자,문장에 문자삽입
        if (ch == eofCh)
            error("Attempt to read past end of file");
        col++;                                                      //열 변수 증가
        if (col >= line.length()) {                                 // 열 변수가 줄길이 넘으면 다음줄로 이동
            try {
                line = input.readLine( );
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            } // try
            if (line == null)                                        // 파일의 끝이면 eofch 붙임
                line = "" + eofCh;
            else {
                // System.out.println(lineno + ":\t" + line);
                lineno++;
                line += eolnCh;                                     //끝이 아니면 줄 총 갯수 증가후 개행문자
            } // if line
            col = 0;                                                // 열 초기화
        } // if col
        return line.charAt(col);
    }


    public Token next( ) {                                          // 다음 토큰 접근
        do {
            if (isLetter(ch) || ch == '_') {                        // 첫문자가 알파벳, '_'인경우, 식별자 또는 키워드
                String spelling = concat(letters + digits + '_');
                return Token.keyword(spelling);
            } else if (isDigit(ch)) {                               //첫 문자가 숫자면 int or double

                String number = concat(digits + '.');               //3) double literal (  .123,   123. 과 같은 숏폼 인식)
                if(number.contains(".") )
                    return Token.mkDoubleLiteral(number);
                return Token.mkIntLiteral(number);                  // .을 포함하면 double, 아니면 int

            } else if (ch == '.') {                                 // .으로 시작하는 숏폼 .123
                ch = nextChar();
                if (isDigit(ch)) {
                    String number = concat(digits + '.');
                    return Token.mkDoubleLiteral("." + number);
                }

            } else switch (ch) {
                case ' ': case '\t': case '\r': case eolnCh:        // 공백,탭 개행문자
                    ch = nextChar();
                    break;

                case '/':                                           // /, /=, 또는 주석처리
                    ch = nextChar();
                    if (ch == '=')  {                               // /=
                        ch = nextChar();
                        return Token.divAssignTok;
                    }


                    if (ch != '*' && ch != '/') return Token.divideTok; // 단순 나누기


                    if (ch == '*') {                                // /* 일 경우 /* */또는 /** */
                        ch=nextChar();
                        if(ch == '*')                               // 1) documented(/** ~ */) comments
                        {
                            ch=nextChar();
                            do {
                                while (ch != '*') {
                                    if(ch!='\n')
                                        comment+=ch;
                                    ch = nextChar();
                                }
                                System.out.println("Documented (/** ~ */) Comments ------>  "+comment);
                                ch = nextChar();
                            } while (ch != '/');
                            comment="";                             //주석 내용 초기화
                            ch = nextChar();
                        }
                        else{                                       // '/* */'스타일 주석
                            do {
                                while (ch != '*') {
                                    if(ch!='\n')
                                        comment+=ch;
                                    ch = nextChar();
                                }
                                System.out.println("Documented (/* ~ */) Comments ------>  "+comment);
                                ch = nextChar();
                            } while (ch != '/');
                            comment="";                             //주석 내용 초기화
                            ch = nextChar();
                        }
                    }
                    // single line comment
                    else if (ch == '/')  {                           // 한줄 주석
                       ch=nextChar();
                       if(ch=='/')                                  // '///' 스타일 주석
                       {
                           do {
                               ch = nextChar();
                               if(ch!='\n')
                                   comment+=ch;
                           } while (ch != eolnCh);
                           System.out.println("Single Line (///) Comments ------>  "+comment);
                           comment="";
                           ch = nextChar();
                       }
                       else {                                       // '//'스타일 주석
                           do {
                               ch = nextChar();
                               if(ch!='\n')
                                   comment+=ch;
                           } while (ch != eolnCh);
                           System.out.println("Single Line (//) Comments ------>  "+comment);
                           comment="";
                           ch = nextChar();
                       }
                    }

                    break;

                case '\'':                                          // 1) character literal
                    char ch1 = nextChar();
                    nextChar(); // get '
                    ch = nextChar();
                    return Token.mkCharLiteral("" + ch1);

                case '"':                                            // 2) string literal
                    ch=nextChar();
                    String str="";
                    while(ch!='"') {
                        str+=ch;
                        ch=nextChar();
                    }
                    ch=nextChar();
                    return Token.mkStringLiteral(str);
                case eofCh: return Token.eofTok;

                case '+':
                    ch = nextChar();
                    if (ch == '=')  {                               // +
                        ch = nextChar();
                        return Token.addAssignTok;
                    }
                    else if (ch == '+')  {                          // ++
                        ch = nextChar();
                        return Token.incrementTok;
                    }
                    return Token.plusTok;

                case '-':
                    ch = nextChar();
                    if (ch == '=')  {                               // -=
                        ch = nextChar();
                        return Token.subAssignTok;
                    }
                    else if (ch == '-')  {                          // --
                        ch = nextChar();
                        return Token.decrementTok;
                    }
                    return Token.minusTok;

                case '*':
                    ch = nextChar();
                    if (ch == '=')  {                               // *=
                        return Token.multAssignTok;
                    }
                    return Token.multiplyTok;

                case '%':
                    ch = nextChar();
                    if (ch == '=')  {                               // %=
                        ch = nextChar();
                        return Token.remAssignTok;
                    }
                    return Token.reminderTok;

                case '(': ch = nextChar();                          // (
                    return Token.leftParenTok;

                case ')': ch = nextChar();                          // )
                    return Token.rightParenTok;

                case '{': ch = nextChar();                          // {
                    return Token.leftBraceTok;

                case '}': ch = nextChar();                          // }
                    return Token.rightBraceTok;

                case ';': ch = nextChar();                          // ;
                    return Token.semicolonTok;

                case ',': ch = nextChar();                          // ,
                    return Token.commaTok;

                case '&': check('&'); return Token.andTok;          // &
                case '|': check('|'); return Token.orTok;           // |

                case '=':                                           // = or ==
                    return chkOpt('=', Token.assignTok,
                            Token.eqeqTok);

                case '<':                                           // < or <=
                    return chkOpt('=', Token.ltTok,
                            Token.lteqTok);
                case '>':                                           // > or >=
                    return chkOpt('=', Token.gtTok,
                            Token.gteqTok);
                case '!':                                           // ! or !=
                    return chkOpt('=', Token.notTok,
                            Token.noteqTok);

                // 2. 추가 연산자: ':'
                case ':':   ch = nextChar();
                    return Token.colonTok;


                default:  error("Illegal character " + ch);

            } // switch
        } while (true);
    } // next


    private boolean isLetter(char c) {
        return (c>='a' && c<='z' || c>='A' && c<='Z');
    }   //문자 c가 알파벳이면 true

    private boolean isDigit(char c) {
        return (c>='0' && c<='9');
    }    //문자 c가 숫자면 true

    private void check(char c) {
        ch = nextChar();
        if (ch != c)
            error("Illegal character, expecting " + c);
        ch = nextChar();
    }

    private Token chkOpt(char c, Token one, Token two) {        //토큰 종류 가르는 함수  ex <,<=  >,>=  !,!=
        ch = nextChar();
        if (ch != c)
            return one;
        ch = nextChar();
        return two;
    }

    private String concat(String set) {
        String r = "";
        do {
            r += ch;
            ch = nextChar();
        } while (set.indexOf(ch) >= 0);             //문자들을 전달된 set에 있는 걸로 만들어서 반환
        return r;
    }

    public void error (String msg) {
        //System.err.print(line);
        //System.err.println("Error: column " + col + " " + msg);
        System.out.println("Error: column " + col + " " + msg);
        ch=nextChar();
        //System.exit(1); //토큰 구조에 해당하지 않는 경우에는 에러 메시지 출력 후 다음 토큰 인식 시도.
    }

    static public void main ( String[] argv ) {
        Scanner lexer = new Scanner(argv[0]);
        Token tok = lexer.next( );
        while (tok != Token.eofTok) {
            //System.out.println(tok.toString());
            System.out.println
                    ("Token -----> "+ tok.value()+
                      "  (  " + tok.number() + " , " + tok.value()+ " , " + lexer.file_name+ " , "+ lexer.lineno + " , " + lexer.col + "   )   ");

            //* 출력 형식은 다음과 같음.
            /*
            Token -----> int (token number, token value, file name, line number, column number)
            Token -----> main (token number, token value, file name, line number, column number)
            ...
            Documented Comments ------> comment contents
            ...
             */

            tok = lexer.next( );
        }
        System.out.println("-------------Scan Finished-------------");

    } // main
}
