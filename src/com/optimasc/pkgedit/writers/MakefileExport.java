package com.optimasc.pkgedit.writers;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.optimasc.pkgedit.targets.ApplicationTarget;
import com.optimasc.pkgedit.targets.LibraryTarget;
import com.optimasc.pkgedit.targets.ProjectInfo;
import com.optimasc.pkgedit.targets.Target;
import com.optimasc.pkgedit.utils.FilenameUtilities;
import com.optimasc.pkgedit.utils.ListUtilities;

public class MakefileExport implements ProjectWriter
{
  public static final String WRITER_NAME = "POSIX Makefile";
  
  /** COMPILER SPECIFIC FLAGS */
  public static final String INCLUDE_DIRECTORY_FLAG = "-I";
  public static final String DEFINE_FLAG = "-D";
  public static final String LINKER_LIBPATH_FLAG = "-L";
  public static final String LINKER_LIBRARY_FLAG = "-l";
  
  public static final String FILENAME = "Makefile";
  
  /* Internal macros */
  protected Hashtable<String,String> macros;
  protected String outFile;
  
  
  public MakefileExport(String outFile)
  {
    macros = new Hashtable<String, String>();
    this.outFile = outFile;
    
  }

  @Override
  public String getName()
  {
    return WRITER_NAME;
  }

  @Override
  public void init(ProjectInfo project)
  {
    /** Only supports in source build with no other output directory */
    macros.put("CMAKE_CURRENT_SOURCE_DIR", ".");
    macros.put("CMAKE_CURRENT_BINARY_DIR", ".");
    macros.put("CMAKE_SOURCE_DIR", ".");
    macros.put("CMAKE_BINARY_DIR", ".");
  }
  
  /** Write a macro definition 
   * 
   * @param macroName The actual macro name
   * @param toolFlag Any tool flag
   * @param values The values to write
   * @param force true if it should be written if empty.
   */
  protected static void writeMacroDef(PrintStream ps,String macroName, String toolFlag, List<String> values, boolean force)
  {
    String s;
    if ((force == false) && (values.size()==0))
      return;
    s = macroName+"=";
    for (int i = 0; i < values.size(); i++)
    {
      s = s + toolFlag + values.get(i)+" ";
    }
    ps.println(s);
  }
  

  @Override
  public void store(ProjectInfo project) throws IOException, UnsupportedOperationException
  {
    Map<String,Target> targets = project.getTargets();
    StringBuffer stringBuffer = new StringBuffer();
    Target target = null;
    List<String> objectFiles = new ArrayList<String>();
    
    /******************************************* Makefile writer *******************************************/
    int counter = 0;
    Iterator<String> it = targets.keySet().iterator();
    while (it.hasNext())
    {
      String output = null;
      PrintStream ps = null;
      objectFiles.clear();
      String key = it.next();
      target = targets.get(key);
      
      if (counter == 0)
      {
        ps = new PrintStream(outFile);
      } else
      {
        ps = new PrintStream(outFile+Integer.toString(counter));
      }
      
      /** WRITE ALL INTERNAL MACROS FIRST */
      for (Enumeration<String> e1 = macros.keys(); e1.hasMoreElements();)
      {
        String key1 = e1.nextElement();
        ps.println(key1+"="+macros.get(key1));
      }
      /** WRITE SPECIFIC INFORMATION FOR LANGUAGES */
      ps.println("INSTALL_PREFIX=.");
      ps.println("STATIC_LIBRARY_PREFIX=lib");
      ps.println("STATIC_LIBRARY_SUFFIX=.a");
      ps.println("STATIC_LIBRARY_FLAGS=");
      ps.println("EXECUTABLE_SUFFIX=");
      ps.println("SHARED_LIBRARY_PREFIX=");
      ps.println("SHARED_LIBRARY_SUFFIX=");
      ps.println("SHARED_LINKER_FLAGS=");
      ps.println("OBJECT_SUFFIX=.o");
      ps.println();

      ps.println("COPY=cp");
      ps.println("RM=rm -f");
      ps.println("MV=mv");
      ps.println("CC=gcc");
      ps.println("AR=ar");
      ps.println("ARFLAGS=rs");
      ps.println("COMPILE_OPTIONS=-W -O");

      ps.println("# Generic definitions ");
      
      /* List object files for all targets */
      List<String> sources = new ArrayList<String>();
      List<String> depends = new ArrayList<String>();
      List<String> outputs = new ArrayList<String>();
      
      
      writeMacroDef(ps, "LIBPATH",LINKER_LIBPATH_FLAG,target.getLinkDirectories(),true);
      writeMacroDef(ps, "INCLUDE_DIRECTORIES",INCLUDE_DIRECTORY_FLAG,target.getIncludeDirectories(),true);
      writeMacroDef(ps, "LDLIBS",LINKER_LIBRARY_FLAG,target.getLibraryFiles(),true);
      
      for (int k = 0; k < target.getSourceFiles().size(); k++)
      {
        sources.add(target.getSourceFiles().get(k));
        objectFiles.add(FilenameUtilities.removeSuffix(target.getSourceFiles().get(k))+"$(OBJECT_SUFFIX)");
      }
      writeMacroDef(ps, "OBJS","",objectFiles,true);
      
      
      for (int k = 0; k < target.getOtherDepedenciesFiles().size(); k++)
      {
        depends.add(target.getOtherDepedenciesFiles().get(k));
      }

      writeMacroDef(ps, "COMPILE_DEFINITIONS",DEFINE_FLAG,target.getCompileDefines(),true);
      ps.println("COMPILE_OPTIONS="+target.getCompileOptions());
      
      ps.println("CFLAGS=$(COMPILE_OPTIONS) $(INCLUDE_DIRECTORIES) $(COMPILE_DEFINITIONS)");
      
      /****************************** all target ******************************/
      ps.println();
      ps.println("all: clean "+target.getTargetName());
      /****************************** the target ******************************/

      ps.println();
      ps.println(target.getTargetName()+": $(OBJS)");
      if (target instanceof ApplicationTarget)
      {
        stringBuffer.setLength(0);
        output = target.getTargetName()+"$(EXECUTABLE_SUFFIX)";
        outputs.add(output);
        /* Add global flags */
        stringBuffer.append("$(CC) $(LDFLAGS) -o "+output+" ");
        stringBuffer.append(ListUtilities.listToString(objectFiles)+" ");
        stringBuffer.append("$(LIBPATH) $(LDLIBS) ");
        ps.println("\t"+stringBuffer.toString());
      } else
      if (target instanceof LibraryTarget)
      {
        stringBuffer.setLength(0);
        output = "$(STATIC_LIBRARY_PREFIX)"+target.getTargetName()+"$(STATIC_LIBRARY_SUFFIX)";
        outputs.add(output);
        /* Add global flags */
        stringBuffer.append("$(AR) $(ARFLAGS) "+output+" $(OBJS)");
        ps.println("\t"+stringBuffer.toString());
      }
      ps.println();
      
      /** Generates all source files */
      
      /** If there is no path location, then we use suffix rules,
       *  otherwise we might right done manually each rule!!!
       */
      for (int k = 0; k < sources.size(); k++)
      {
        ps.println(FilenameUtilities.removeSuffix(sources.get(k))+"$(OBJECT_SUFFIX): "+sources.get(k)+" "+ListUtilities.listToString(depends));
      }
      
      /****************************** Clean target ******************************/
      ps.println();
      ps.println("clean:");
      ps.println("\t$(RM) $(OBJS) "+ListUtilities.listToString(outputs));
      
      /****************************** Suffix rules ******************************/
      ps.println();
      ps.println(".SUFFIXES: .c $(OBJECT_SUFFIX)");
      ps.println(".c$(OBJECT_SUFFIX):");
      ps.println("\t$(CC) $(CFLAGS) -o $*$(OBJECT_SUFFIX) -c $<");
      
      counter++;
      ps.close();
      
    }    
    
  }

  @Override
  public void done(ProjectInfo project)
  {
  }

  @Override
  public boolean isSupportedLanguage(String language)
  {
    return false;
  }

}
