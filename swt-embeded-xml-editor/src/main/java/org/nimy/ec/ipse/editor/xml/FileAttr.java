package org.nimy.ec.ipse.editor.xml;

import java.io.File;
import java.io.IOException;

public class FileAttr
{
  public static void main(String[] args)
  {
    File file = new File("D:\\");
    if (file.exists()) {
      if (file.isDirectory())
        displayDirectory(file);
      else
        displayContent(file);
    }
    else {
      System.out.println(file.getAbsolutePath());
      System.out.println(".readme file is not existed.");
    }
  }

  public static void displayDirectory(File file)
  {
    File[] list = file.listFiles();
    int i = 0;
    for (File f : list) {
      if (f.isDirectory())
        System.out.println(i + " Directory : " + f.getName() + " path info: " + f.getAbsolutePath());
      else {
        System.out.println(i + " File : " + f.getName() + " path info: " + f.getAbsolutePath());
      }
      i++;
    }
  }

  public static void displayContent(File file)
  {
    try {
      System.out.println("File : " + file.getName() + " path info: " + file.getAbsolutePath() + " Canonical path: " + file.getCanonicalPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}