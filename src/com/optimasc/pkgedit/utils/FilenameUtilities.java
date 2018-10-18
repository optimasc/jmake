package com.optimasc.pkgedit.utils;

public class FilenameUtilities
{

  public static String removeSuffix(String s)
  {

    String separator = "/";
    String filename;

    // Remove the path up to the filename.
    int lastSeparatorIndex = s.lastIndexOf(separator);
    if (lastSeparatorIndex == -1)
    {
      filename = s;
    } else
    {
      filename = s.substring(lastSeparatorIndex + 1);
    }

    // Remove the extension.
    int extensionIndex = filename.lastIndexOf(".");
    if (extensionIndex == -1)
      return filename;

    return filename.substring(0, extensionIndex);
  }

}
