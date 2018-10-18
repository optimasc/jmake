package com.optimasc.pkgedit.targets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;


/** Represents a target to generate */
public abstract class AbstractTarget implements Target
{
  protected List<String> libraryFiles;
  protected String targetName;
  /* Should only contain the source code files */
  protected List<String> sourceFiles;
  /* Should contain the extra depedencies associated with the source code files. */
  protected List<String> otherDepedenciesFiles;
  protected List<String> includeDirectories;
  protected List<String> compileDefines;
  protected String compileOptions;
  protected List<String> linkDirectories;
  protected List<String> objectFiles;
  protected Hashtable<String,Object> userData;
  
  
  public AbstractTarget(String name)
  {
    this.targetName = name;
    sourceFiles = new Vector<String>();
    this.includeDirectories = new Vector<String>();
    compileDefines = new Vector<String>();
    compileOptions = "";
    objectFiles = new Vector<String>();
    otherDepedenciesFiles = new Vector<String>();
    userData = new Hashtable<String,Object>();
    libraryFiles = new Vector<String>();
    linkDirectories = new ArrayList<String>();
  }
  
  
  public List<String> getObjectFiles()
  {
    return objectFiles;
  }



  public void setObjectFiles(List<String> objectFiles)
  {
    this.objectFiles = objectFiles;
  }


  

  public List<String> getOtherDepedenciesFiles()
  {
    return otherDepedenciesFiles;
  }



  public void setOtherDepedenciesFiles(List<String> otherDepedenciesFiles)
  {
    this.otherDepedenciesFiles = otherDepedenciesFiles;
  }



  public String getTargetName()
  {
    return targetName;
  }
  
  public void setTargetName(String targetName)
  {
    this.targetName = targetName;
  }
  
  public List<String> getSourceFiles()
  {
    return sourceFiles;
  }
  
  
  public void setSourceFiles(List<String> sourceFiles)
  {
    this.sourceFiles = sourceFiles;
  }
  
  public List<String> getIncludeDirectories()
  {
    return includeDirectories;
  }
  public void setIncludeDirectories(List<String> includeFiles)
  {
    this.includeDirectories = includeFiles;
  }
  public List<String> getCompileDefines()
  {
    return compileDefines;
  }
  
  public void setCompileDefines(List<String> compileDefines)
  {
    this.compileDefines = compileDefines;
  }
  
  public String getCompileOptions()
  {
    return compileOptions;
  }
  
  public void setCompileOptions(String compileOptions)
  {
    this.compileOptions = compileOptions;
  }


  @Override
  public Object getUserData(String key)
  {
    return userData.get(key);
  }


  @Override
  public Object setUserData(String key, Object data)
  {
    return userData.put(key,data);
  }
  
  public List<String> getLibraryFiles()
  {
    return libraryFiles;
  }

  public void setLibraryFiles(List<String> libraryFiles)
  {
    this.libraryFiles = libraryFiles;
  }


@Override
public List<String> getLinkDirectories() {
	return this.linkDirectories;
}


@Override
public void setLinkDirectories(List<String> linkDirectories) {
	this.linkDirectories = linkDirectories;
}
  
  
  
  
}
