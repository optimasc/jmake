package com.optimasc.pkgedit.readers.cmake;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Vector;

public class CMakeFileParser
{
  Vector<CMakeCommand> commands = new Vector<CMakeCommand>();
  protected String filename;
  protected StreamTokenizer tokenizer;
  
  public static final String FILEMAME = "CMakeLists.txt";
  
  public static final char TOKEN_LPAREN = '(';
  public static final char TOKEN_RPAREN = ')';
  public static final char COMMENT_CHAR = '#';

  public static boolean isValidIdentifier(String id)
  {
    char ch;
    ch = id.charAt(0);
    /* Verify identifier validity */
    if (((ch >= 'A') && (ch <= 'Z')) || (ch == '_') || ((ch >= 'a') && (ch <= 'z')))
    {
      for (int i = 1; i <= id.length(); i++)
      {
        ch = id.charAt(i);
        if (((ch >= 'A') && (ch <= 'Z')) || (ch == '_') || ((ch >= 'a') && (ch <= 'z'))
            || ((ch >= '0') && (ch <= '9')))
        {
          return true;
        }
      }
    }
    return false;
  }

  public void error(int lineNumber, String message)
  {
    System.err.println(filename + ":" + lineNumber + ":1: error: " + message);
    System.exit(1);
  }

  public void expect(int token) throws IOException
  {
    int readToken = tokenizer.nextToken();
    if (readToken != token) 
    {
      if (token == StreamTokenizer.TT_WORD)
      {
        error(tokenizer.lineno(), "Unexpected token: " + tokenizer.sval);
      } else
      {
        error(tokenizer.lineno(), "Unexpected token: " + (char)(token));
      }
    }
  }


  public void parse() throws IOException
  {
    int token;
    String command;
    Vector<String> arguments = null;

    while ((token = tokenizer.nextToken()) != StreamTokenizer.TT_EOF)
    {
      switch (token)
      {
        case StreamTokenizer.TT_WORD:
          /* Command invocation */
          command = tokenizer.sval;
          if (isValidIdentifier(command) == false)
          {
            error(tokenizer.lineno(),
                "Invalid identifier must be composed of [A-Za-z_][A-Za-z0-9_]*: " + tokenizer.sval);
          }
          /* Check arguments. */
          expect(TOKEN_LPAREN);
          token = tokenizer.nextToken();
          arguments = new Vector<String>();
          while (token != TOKEN_RPAREN)
          {
            if (token == StreamTokenizer.TT_NUMBER)
            {
              arguments.add(Double.toString(tokenizer.nval));
            } else
            if (token == StreamTokenizer.TT_WORD)
            {
              arguments.add(tokenizer.sval);
            }
            token = tokenizer.nextToken();
          }
          tokenizer.pushBack();
          expect(TOKEN_RPAREN);
          CMakeCommand commandObj = new CMakeCommand(command.toLowerCase(),tokenizer.lineno());
          commandObj.setArguments(arguments);
          commands.add(commandObj);
          break;
        case StreamTokenizer.TT_NUMBER:
          break;
        case StreamTokenizer.TT_EOF:
        case StreamTokenizer.TT_EOL:
          break;
        default:
          break;
      }
    }

  }

  public CMakeFileParser(String filespec) throws IOException
  {
    tokenizer = new StreamTokenizer(new InputStreamReader(new FileInputStream(filespec),
        "US-ASCII"));
    commands = new Vector<CMakeCommand>();
    tokenizer.commentChar(COMMENT_CHAR);
    tokenizer.wordChars('_', '_');
    tokenizer.wordChars('$', '$');
    tokenizer.wordChars('{', '{');
    tokenizer.wordChars('}', '}');
    tokenizer.wordChars('/', '/');
    tokenizer.wordChars('=','=');
    tokenizer.eolIsSignificant(true);
    tokenizer.lowerCaseMode(false);
    filename = new File(filespec).getName();
  }

  public void accept(final CMakeCommandVisitor visitor)
  {
    for (int i = 0; i <= commands.size(); i++)
    {
      CMakeCommand cmd = (CMakeCommand) commands.elementAt(i);
      cmd.accept(visitor);
    }
    //    visitor.visit(this);
  }

  public Vector<CMakeCommand> getCommands()
  {
    return commands;
  }
  
  
}
