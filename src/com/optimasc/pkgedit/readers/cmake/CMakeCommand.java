package com.optimasc.pkgedit.readers.cmake;
import java.util.Vector;


public class CMakeCommand
{
  /** The name of the command. */
  protected String name;
  /** The command arguments */
  protected Vector<String> arguments;
  protected int lineNumber;
  
  public CMakeCommand(String commandName, int lineno)
  {
    this.name = commandName;
    lineNumber = lineno;
  }
  
  
  
  public Vector<String> getArguments()
  {
    return arguments;
  }



  public void setArguments(Vector<String> arguments)
  {
    this.arguments = arguments;
  }



  public String getName()
  {
    return name;
  }



  public void accept(CMakeCommandVisitor visitor)
  {
    visitor.visit(this);
  }
}
