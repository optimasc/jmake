import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.optimasc.pkgedit.readers.cmake.CMakeFileParser;
import com.optimasc.pkgedit.readers.cmake.CMakeFileImport;
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
      inputName = args[0];
    }
    CMakeFileImport cmakeImporter = new CMakeFileImport(inputName);
    ProjectInfo project = cmakeImporter.parse();
    ProjectWriter writer = new MakefileExport();
    writer.init(project);
    writer.store(project);
    writer.done(project);
  }

}
