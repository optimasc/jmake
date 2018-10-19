import java.io.File;
import java.io.IOException;

import com.optimasc.pkgedit.readers.cmake.CMakeFileImport;
import com.optimasc.pkgedit.readers.cmake.CMakeFileParser;
import com.optimasc.pkgedit.targets.ProjectInfo;
import com.optimasc.pkgedit.writers.MakefileExport;
import com.optimasc.pkgedit.writers.ProjectWriter;

public class JMake
{

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException
  {
    String inputName = CMakeFileParser.FILEMAME;
    String outFile   = MakefileExport.FILENAME; 
    if (args.length == 0)
    {
      File currentDir = new File(System.getProperty("user.dir"));
      File cmakeList = new File(currentDir, CMakeFileParser.FILEMAME);
      if (cmakeList.exists() == false)
      {
        System.err.println("Fatal: Cannot find '" + CMakeFileParser.FILEMAME
            + "' in current working directory.");
        System.exit(1);
      }
    } else
    {
      int i = 0;
      while (i < args.length)
      {
        if (args[i].equals("--help"))
        {
          System.out.println("Usage: jmake [-o outfile] [file]");
          System.out.println("Read [file] or CMakeLists.txt in current directory CMake command list");
          System.out.println();
          System.out.println("-o outfile\tUse the pathname outfile instead of Makefile");
          System.exit(1);
        }
        else
        if (args[i].equals("-o"))
        {
          i++;
          if (i < args.length)
          {
            outFile = args[i]; 
          } else
          {
            System.err.println("Missing output pathname argument.");
            System.exit(1);
          }
        } 
        else
        {
          inputName = args[i];
        }
        i++;
      }
    }
    
    if (outFile.contains(CMakeFileParser.FILEMAME))
    {
        System.err.println("Trying to write "+CMakeFileParser.FILEMAME+", options probably wrong.");
        System.exit(1);
    }
    CMakeFileImport cmakeImporter = new CMakeFileImport(inputName);
    ProjectInfo project = cmakeImporter.parse();
    ProjectWriter writer = new MakefileExport(outFile);
    writer.init(project);
    writer.store(project);
    writer.done(project);
  }

}
