package org.createlab.log.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LineReader
   {
   private static final Logger LOG = Logger.getLogger(LineReader.class);

   @NotNull
   private final File file;

   public LineReader(@NotNull final File file)
      {
      this.file = file;
      }

   public void read(@NotNull final LineProcessor lineProcessor)
      {
      try
         {
         final FileReader fileReader = new FileReader(file);
         final BufferedReader bufferedReader = new BufferedReader(fileReader);
         String line;
         lineProcessor.preProcess();
         while ((line = bufferedReader.readLine()) != null)
            {
            lineProcessor.processLine(line);
            }
         lineProcessor.postProcess();

         bufferedReader.close();
         }
      catch (FileNotFoundException e)
         {
         LOG.error("FileNotFoundException caught while trying to create a FileReader for file [" + file + "]", e);
         }
      catch (IOException e)
         {
         LOG.error("IOException caught while trying to read file [" + file + "]", e);
         }
      }
   }
