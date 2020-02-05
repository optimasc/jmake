package com.optimasc.pkgedit.targets;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;


/** Contains project related information. */
public class DefaultProjectInfo implements ProjectInfo
{
  /** The project name */
  protected String name;
  /** The project major version */
  protected String versionMajor;
  /** The project minor version */
  protected String versionMinor;
  /** The project patch version */
  protected String versionPatch;
  /** The project root directory */
  protected String rootDirectory;
  /** Targets defined for this project. */
  protected Map<String, Target> targets;
  
  
  

  public DefaultProjectInfo()
  {
    super();
    targets = new Hashtable<String,Target>();
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getVersionMajor()
  {
    return versionMajor;
  }

  public void setVersionMajor(String versionMajor)
  {
    this.versionMajor = versionMajor;
  }

  public String getVersionMinor()
  {
    return versionMinor;
  }

  public void setVersionMinor(String versionMinor)
  {
    this.versionMinor = versionMinor;
  }

  public String getVersionPatch()
  {
    return versionPatch;
  }

  public void setVersionPatch(String versionPatch)
  {
    this.versionPatch = versionPatch;
  }

  public String getRootDirectory()
  {
    return rootDirectory;
  }

  public void setRootDirectory(String rootDirectory)
  {
    this.rootDirectory = rootDirectory;
  }

  @Override
  public void addTarget(String name, Target target)
  {
    targets.put(name, target);
  }

  @Override
  public Map<String, Target> getTargets()
  {
    return Collections.unmodifiableMap(targets);
  }

  @Override
  public Target getTarget(String name)
  {
    return targets.get(name);
  }

  @Override
  public String getVersion()
  {
    StringBuffer buffer = new StringBuffer();
    if (versionMajor!=null)
    {
      buffer.append(versionMajor);
      if (versionMinor!=null)
      {
        buffer.append(".");
        buffer.append(versionMinor);
        if (versionPatch!=null)
        {
          buffer.append(".");
          buffer.append(versionPatch);
        }
      }
    }
    return buffer.toString();
  }

}
