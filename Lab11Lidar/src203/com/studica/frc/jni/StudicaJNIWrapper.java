package com.studica.frc.jni;

import java.io.IOException;

import edu.wpi.first.wpiutil.RuntimeLoader;

public class StudicaJNIWrapper {
  static boolean libraryLoaded = false;
  static RuntimeLoader<StudicaJNIWrapper> loader = null;

  
  static {
    if (!libraryLoaded) {
      try {
        loader = new RuntimeLoader<>("StudicaDriver", RuntimeLoader.getDefaultExtractionRoot(), StudicaJNIWrapper.class);
        loader.loadLibrary();
      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(1);
      }
      libraryLoaded = true;
    }
  }
}