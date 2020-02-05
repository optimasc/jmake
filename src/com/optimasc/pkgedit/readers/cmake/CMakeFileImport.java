package com.optimasc.pkgedit.readers.cmake;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.optimasc.pkgedit.targets.ApplicationTarget;
import com.optimasc.pkgedit.targets.DefaultProjectInfo;
import com.optimasc.pkgedit.targets.LibraryTarget;
import com.optimasc.pkgedit.targets.ProjectInfo;
import com.optimasc.pkgedit.targets.Target;
import com.optimasc.pkgedit.utils.ListUtilities;

/**
 * Simple POSIX Makefile generator from CMake files with following limitations:
 * Only supports building in current directory location.
 * 
 * @author Carl Eric Codere
 * 
 */
public class CMakeFileImport implements CMakeCommandVisitor
{
  public static final String COMMAND_ADD_COMPILE_OPTIONS = "add_compile_options";
  public static final String COMMAND_ADD_EXECUTABLE = "add_executable";
  public static final String COMMAND_ADD_LIBRARY = "add_library";
  public static final String COMMAND_LINK_LIBRARIES = "link_libraries";
  public static final String COMMAND_INCLUDE_DIRECTORIES = "include_directories";
  public static final String COMMAND_SET = "set";
  public static final String COMMAND_TARGET_COMPILE_OPTIONS = "target_compile_options";
  public static final String COMMAND_TARGET_INCLUDE_DIRECTORIES = "target_include_directories";
  public static final String COMMAND_TARGET_LINK_LIBRARIES = "target_link_libraries";
  public static final String COMMAND_LINK_DIRECTORIES = "link_directories";
  public static final String COMMAND_TARGET_COMPILE_DEFINITIONS = "target_compile_definitions";
  /** TODO Commands */
  public static final String COMMAND_ADD_DEFINITIONS = "add_definitions";
  public static final String COMMAND_ADD_SUBDIRECTORY = "add_subdirectory";
  public static final String COMMAND_ADD_CUSTOM_TARGET = "add_custom_target";
  public static final String COMMAND_EXPORT = "export";
  public static final String COMMAND_INSTALL = "install";
  public static final String COMMAND_PROJECT = "project";

  public static final String LANGUAGE_C = "C";
  public static final String LANGUAGE_PASCAL = "PASCAL";
  public static final String LANGUAGE_JAVA = "JAVA";

  protected String language;
  /* Internal macros */
  protected Hashtable<String, String> macros;
  /* User defined macros using SET command. */
  protected Hashtable<String, Vector<String>> userMacros;
  protected List<String> includeDirectories;
  protected String compileOptions;
  protected List<String> linkDirectories;
  protected List<String> linkLibraries;
  protected List<String> compileDefines;
  protected ProjectInfo project;
  protected String filename;

  public CMakeFileImport(String filename) throws IOException
  {
    macros = new Hashtable<String, String>();
    userMacros = new Hashtable<String, Vector<String>>();
    includeDirectories = new Vector<String>();
    linkDirectories = new Vector<String>();
    linkLibraries = new Vector<String>();
    compileDefines = new Vector<String>();
    compileOptions = "";
    project = new DefaultProjectInfo();
    this.filename = filename;

    language = LANGUAGE_C;
  }

  public static List<String> removeDuplicates(List<String> list)
  {

    // Store unique items in result.
    ArrayList<String> result = new ArrayList<String>();

    // Record encountered Strings in HashSet.
    HashSet<String> set = new HashSet<String>();

    // Loop over argument list.
    for (String item : list)
    {

      // If String is not in set, add it to the list and the set.
      if (!set.contains(item))
      {
        result.add(item);
        set.add(item);
      }
    }
    return result;
  }

  protected String expandUserVariables(String str)
  {
    String value = new String(str);
    for (Enumeration<String> e = userMacros.keys(); e.hasMoreElements();)
    {
      String key = e.nextElement();
      String v = ListUtilities.listToString(userMacros.get(key));
      value = value.replace("${" + key + "}", v);
    }
    return value;
  }
  
  protected String expandGeneratorVariables(String str)
  {
    String value = new String(str);

    /* Install interface is not supported, only build interface */
    if (value.startsWith("$<INSTALL_INTERFACE:"))
    {
      return "";
    }
    
    /* Install interface is not supported, only build interface */
    if (value.startsWith("$<BUILD_INTERFACE:"))
    {
      return value.substring("$<BUILD_INTERFACE:".length(), str.length()-1);
    }
    return value;
  }
  

  /** Completely expand variables. */
  protected String expandVariables(String str)
  {
    String value = new String(str);
    while (value.indexOf("${") >= 0)
    {
      for (Enumeration<String> e = macros.keys(); e.hasMoreElements();)
      {
        String key = e.nextElement();
        value = value.replace("${" + key + "}", macros.get(key));
      }

      for (Enumeration<String> e = userMacros.keys(); e.hasMoreElements();)
      {
        String key = e.nextElement();
        String v = ListUtilities.listToString(userMacros.get(key));
        value = value.replace("${" + key + "}", v);
      }
    }
    return value;
  }

  /** Expands all user defined macros to get all the sources files. */
  protected Vector<String> getSourceFiles(Vector<String> args)
  {
    String value;
    Vector<String> result = new Vector<String>();
    for (int i = 0; i < args.size(); i++)
    {
      value = new String(args.elementAt(i));
      value = expandVariables(value);
      result.addElement(value);
    }
    return result;
  }

  @Override
  public void visit(CMakeCommand v)
  {

  }

  public void error(CMakeCommand cmd, String message)
  {
    System.err.println(CMakeFileParser.FILEMAME + ":" + cmd.lineNumber + ":1: error: " + message);
    System.exit(-1);
  }

  public void warning(CMakeCommand cmd, String message)
  {
    System.err.println(CMakeFileParser.FILEMAME + ":" + cmd.lineNumber + ":1: warning: " + message);
  }

  /**
   * Validates that the target is present, and optionally check if it is
   * defined.
   * 
   * @param cmd
   * @param targetMustExist
   *          true if the target must exist, if it does not error is thrown.
   * @return The actual Target
   */
  protected Target validateTarget(CMakeCommand cmd, boolean targetMustExist)
  {
    Vector<String> args = cmd.getArguments();
    if (args.size() == 0)
    {
      error(cmd, "command requires a target name");
    }
    String targetName = args.elementAt(0);
    if (targetMustExist == true)
    {
      if (project.getTargets().containsKey(targetName) == false)
      {
        error(cmd, "target does not exist: '" + targetName + "'");
      }
    }
    return project.getTargets().get(targetName);
  }

  /**
   * Returns true if this filename ends with a language specific suffix.
   * 
   * @param lang
   *          The language to check against.
   * @param filename
   *          The filename to check
   * @return true if this is a file that can be compiled by the compiler
   *         directly.
   */
  public static boolean isLanguageSuffix(String lang, String filename)
  {
    if (lang.equals(LANGUAGE_C))
    {
      if (filename.endsWith(".c"))
        return true;
    }
    if (lang.equals(LANGUAGE_JAVA))
    {
      if (filename.endsWith(".java"))
        return true;
    }
    if (lang.equals(LANGUAGE_PASCAL))
    {
      if (filename.endsWith(".pas"))
        return true;
      if (filename.endsWith(".pp"))
        return true;
      if (filename.endsWith(".p"))
        return true;
    }
    return false;
  }

  /**
   * Creates a target and populates it accordingly and adds it to the list of
   * targets.
   * 
   * @param cmd
   * @param isLibrary
   *          true if the target is a library.
   * @return A created target.
   */
  protected Target makeTarget(CMakeCommand cmd, boolean isLibrary)
  {
    Target target = null;
    String value = null;
    Vector<String> args = cmd.getArguments();
    Vector<String> argElements = new Vector<String>();
    if (args.size() == 0)
    {
      error(cmd, "command requires a target name");
    }
    String targetName = args.elementAt(0);
    if (isLibrary)
    {
      target = new LibraryTarget(targetName);
    } else
    {
      target = new ApplicationTarget(targetName);
    }

    if (args.size() == 1)
    {
      error(cmd, "At least one source file must be present in the target '" + targetName + "'");
    }

    /* Check if shared or module is defined, if so return an error, as not supported yet! */
    int startIndex = 1;
    if (isLibrary)
    {
      value = args.get(1);
      if (value.equals("SHARED") || value.equals("MODULE"))
      {
        error(cmd, "Library type '" + value + "' is not supported.");
      }
      /* Skip this value. */
      if (value.equals("STATIC"))
      {
        startIndex = 2;
      }
    }

    for (int j = startIndex; j < args.size(); j++)
    {
      argElements.add(args.elementAt(j));
    }
    /* Convert this to individual source code */
    Vector<String> sourceFiles = new Vector<String>();
    Vector<String> otherDependencies = new Vector<String>();
    for (int j = 0; j < argElements.size(); j++)
    {
      value = expandUserVariables(argElements.elementAt(j));
      String[] values = value.split("[ \t]+");
      for (int k = 0; k < values.length; k++)
      {
        /* Only add real source files, other dependencies are added in other
         * section.
         */
        if (isLanguageSuffix(language, values[k]))
        {
          sourceFiles.add(values[k]);
        } else
        {
          otherDependencies.add(values[k]);
        }
      }
    }
    if (ListUtilities.hasDuplicate(sourceFiles))
    {
      warning(cmd, "Duplicate files found in source list.");
    }
    if (ListUtilities.hasDuplicate(otherDependencies))
    {
      warning(cmd, "Duplicate files found in source list.");
    }

    target.setSourceFiles(sourceFiles);

    target.setOtherDepedenciesFiles(otherDependencies);

    /* Add global values to the target. */

    for (int j = 0; j < includeDirectories.size(); j++)
    {
      target.getIncludeDirectories().add(includeDirectories.get(j));
    }
    for (int j = 0; j < linkLibraries.size(); j++)
    {
      target.getLibraryFiles().add(linkLibraries.get(j));
    }
    for (int j = 0; j < linkDirectories.size(); j++)
    {
      target.getLinkDirectories().add(linkDirectories.get(j));
    }
    for (int j = 0; j < compileDefines.size(); j++)
    {
      target.getCompileDefines().add(compileDefines.get(j));
    }

    target.setCompileOptions(compileOptions + target.getCompileOptions());

    project.addTarget(targetName, target);
    return target;
  }
  
  public static boolean isAllUpperCase(CharSequence s)
  {
    for (int i = 0; i < s.length(); i++)
    {
      if (Character.isLowerCase(s.charAt(i))==true)
      {
        return false;
      }
    }
    return true;
  }
  
  protected Target makeInstall(CMakeCommand cmd)
  {
    Target target = null;
    String value = null;
    Vector<String> args = cmd.getArguments();
    Vector<String> argElements = new Vector<String>();
    if (args.size() == 0)
    {
      error(cmd, "command requires additional argument.");
    }
    
    if (args.get(0).equals("TARGETS"))
    {
      /* Check until capital letter word only. */
      int i = 1;
      /* Get all targets */
      while (i <= args.size())
      {
        value = args.get(i);
        if (isAllUpperCase(value)==false)
        {
          argElements.add(value);
        } else
        {
          break;
        }
        i++;
      }
      /** All target elements are defined now. */
      
    } else
    if (args.get(0).equals("FILES"))
    {
    } else
    if (args.get(0).equals("PROGRAMS"))
    {
      
    } else
    if (args.get(0).equals("DIRECTORY"))
    {
      
    } else
    {
      warning(cmd, "Unsupported '"+args.get(0)+"' install type.");
    }
    return null;
  }
  

  /** Returns the macro name reference, if this macro is non empty. */
  protected String getMacroDecl(String macroName, List<String> values)
  {
    if (values.size() == 0)
      return "";
    return "$(" + macroName + ")";
  }

  public ProjectInfo parse() throws IOException
  {
    CMakeFileParser cmakeFile = new CMakeFileParser(filename);
    cmakeFile.parse();
    generate(cmakeFile.getCommands());
    return project;
  }

  protected void generate(Vector<CMakeCommand> elements)
  {
    CMakeCommand cmd;
    String value = null;
    Target target = null;
    Vector<String> arguments = null;
    Vector<String> newArguments = null;
    Vector<String> generatorArguments = null;
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = 0; i < elements.size(); i++)
    {
      cmd = elements.elementAt(i);
      arguments = cmd.getArguments();
      /* expand user macros */
      newArguments = new Vector<String>();
      for (int j = 0; j < arguments.size(); j++)
      {
        value = expandUserVariables(arguments.elementAt(j));
        String[] values = value.split("[ \t]+");
        for (int k = 0; k < values.length; k++)
        {
          newArguments.add(values[k]);
        }
      }
      arguments = newArguments;
      /* expand generator expressions */
      generatorArguments = new Vector<String>();
      for (int j = 0; j < arguments.size(); j++)
      {
        value = expandGeneratorVariables(arguments.elementAt(j));
        if (value.length()>0)
        {
          generatorArguments.add(value);
        }
      }
      arguments = generatorArguments;
      

      if (cmd.getName().equals(COMMAND_TARGET_COMPILE_DEFINITIONS))
      {
        target = validateTarget(cmd, true);
        for (int j = 1; j < arguments.size(); j++)
        {
          value = arguments.elementAt(j);
          if (value.equals("INTERFACE") ||
              value.equals("PRIVATE") || value.equals("PUBLIC"))
          {
            warning(cmd, "'" + value + "' is ignored.");
            continue;
          }
          target.getCompileDefines().add(value);
        }
      }
      else if (cmd.getName().equals(COMMAND_LINK_DIRECTORIES))
      {
        for (int j = 0; j < arguments.size(); j++)
        {
          linkDirectories.add(arguments.elementAt(j));
        }
      }
      /** TODO Install to implement. */
      else if (cmd.getName().equals(COMMAND_INSTALL))
      {
        makeInstall(cmd);
        if (arguments.size()==0)
        {
          error(cmd,"'TARGETS' in install is missing.");
        }
//        while 
      }
      else if (cmd.getName().equals(COMMAND_LINK_LIBRARIES))
      {
        for (int j = 0; j < arguments.size(); j++)
        {
          linkLibraries.add(arguments.elementAt(j));
        }
      }
      else if (cmd.getName().equals(COMMAND_TARGET_COMPILE_OPTIONS))
      {
        target = validateTarget(cmd, true);
        stringBuffer.setLength(0);
        for (int j = 1; j < arguments.size(); j++)
        {
          value = arguments.elementAt(j);
          if (value.equals("BEFORE") || value.equals("INTERFACE") ||
              value.equals("PRIVATE") || value.equals("PUBLIC"))
          {
            warning(cmd, "'" + value + "' is ignored.");
            continue;
          }
          stringBuffer.append(value);
          stringBuffer.append(' ');
        }
        target.setCompileOptions(target.getCompileOptions() + ' ' + stringBuffer.toString());
      }
      else if (cmd.getName().equals(COMMAND_TARGET_INCLUDE_DIRECTORIES))
      {
        target = validateTarget(cmd, true);
        for (int j = 1; j < arguments.size(); j++)
        {
          value = arguments.elementAt(j);
          if (value.equals("SYSTEM") || value.equals("BEFORE") || value.equals("INTERFACE") ||
              value.equals("PRIVATE") || value.equals("PUBLIC"))
          {
            warning(cmd, "'" + value + "' is ignored.");
            continue;
          }
          target.getIncludeDirectories().add(value);
        }
      }
      else if (cmd.getName().equals(COMMAND_TARGET_LINK_LIBRARIES))
      {
        target = validateTarget(cmd, true);
        for (int j = 1; j < arguments.size(); j++)
        {
          target.getLibraryFiles().add(arguments.elementAt(j));
        }
      }
      else if (cmd.getName().equals(COMMAND_ADD_EXECUTABLE))
      {
        target = makeTarget(cmd, false);
      }
      else if (cmd.getName().equals(COMMAND_ADD_LIBRARY))
      {
        target = makeTarget(cmd, true);
      }
      else if (cmd.getName().equals(COMMAND_ADD_COMPILE_OPTIONS))
      {
        for (int j = 0; j < arguments.size(); j++)
        {
          compileOptions += " " + arguments.elementAt(j);
        }
      }
      else if (cmd.getName().equals(COMMAND_PROJECT))
      {
        if (arguments.size()==0)
        {
          error(cmd,"Project name not defined.");
        }
        else
        {
           value = arguments.elementAt(0);
           project.setName(value);
           if (arguments.size() > 2)
           {
             value = arguments.elementAt(1);
             if (value.equals("VERSION"))
             {
               /* get the version information */
               value = arguments.elementAt(2);
               String version[] = value.split("\\.");
               if (version.length == 0)
               {
                 error(cmd, "'VERSION' has missing components.");
               }
               if (version.length > 0)
               {
                 try 
                 {
                   int intValue = Integer.parseInt(version[0]);
                 } catch (NumberFormatException e)
                 {
                   error(cmd, "'VERSION' components must be integer values.");
                 }
                 project.setVersionMajor(version[0]);
               }
               if (version.length > 1)
               {
                 try 
                 {
                   int intValue = Integer.parseInt(version[1]);
                 } catch (NumberFormatException e)
                 {
                   error(cmd, "'VERSION' components must be integer values.");
                 }
                 project.setVersionMinor(version[1]);
               }
               if (version.length > 2)
               {
                 try 
                 {
                   int intValue = Integer.parseInt(version[2]);
                 } catch (NumberFormatException e)
                 {
                   error(cmd, "'VERSION' components must be integer values.");
                 }
                 project.setVersionPatch(version[2]);
               }
             }
           }
        }
      }
      else if (cmd.getName().equals(COMMAND_ADD_DEFINITIONS))
      {
        for (int j = 0; j < arguments.size(); j++)
        {
          compileDefines.add(arguments.elementAt(j));
        }
      }
      else if (cmd.getName().equals(COMMAND_SET))
      {
        Vector<String> args = cmd.getArguments();
        Vector<String> argElements = new Vector<String>();
        if (args.size() == 0)
        {
          error(cmd, "set requires a variable name");
        }
        String macroName = args.elementAt(0);
        for (int j = 1; j < args.size(); j++)
        {
          argElements.add(args.elementAt(j));
        }
        userMacros.put(macroName, argElements);
      }
      else if (cmd.getName().equals(COMMAND_INCLUDE_DIRECTORIES))
      {
        for (int j = 0; j < arguments.size(); j++)
        {
          includeDirectories.add(arguments.elementAt(j));
        }
      }
    } /* end for */
    /* Actually write the data. */
  }
}
