/* SoundbankReader.java -- Soundbank file reading services
   Copyright (C) 2005 Free Software Foundation, Inc.

This file is part of GNU Classpath.

GNU Classpath is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2, or (at your option)
any later version.

GNU Classpath is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with GNU Classpath; see the file COPYING.  If not, write to the
Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
02110-1301 USA.

Linking this library statically or dynamically with other modules is
making a combined work based on this library.  Thus, the terms and
conditions of the GNU General Public License cover the whole
combination.

As a special exception, the copyright holders of this library give you
permission to link this library with independent modules to produce an
executable, regardless of the license terms of these independent
modules, and to copy and distribute the resulting executable under
terms of your choice, provided that you also meet, for each linked
independent module, the terms and conditions of the license of that
module.  An independent module is a module which is not derived from
or based on this library.  If you modify this library, you may extend
this exception to your version of the library, but you are not
obligated to do so.  If you do not wish to do so, delete this
exception statement from your version. */


package arco.present.aprs.reader.core.sound.midi.spi;


import arco.present.aprs.reader.core.sound.midi.InvalidMidiDataException;
import arco.present.aprs.reader.core.sound.midi.Soundbank;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The SoundbankReader abstract class defines the methods to be provided
 * by a soundbank file reader.
 * 
 * @author Anthony Green (green@redhat.com)
 * @since 1.3
 *
 */
public abstract class SoundbankReader
{
  /**
   * Get a Soundbank from the given URL.
   * 
   * @param url from which to read the Soundbank
   * 
   * @return the Soundbank object
   * 
   * @throws InvalidMidiDataException if the data provided by url cannot be recognized
   * @throws IOException if the data provided by url cannot be read
   */
  public abstract Soundbank getSoundbank(URL url)
    throws InvalidMidiDataException, IOException;
  
  /**
   * Get a Soundbank from the given InputStream.
   * 
   * @param stream from which to read the Soundbank
   * 
   * @return the Soundbank object
   * 
   * @throws InvalidMidiDataException if the data provided by InputStream cannot be recognized
   * @throws IOException if the data provided by InputStream cannot be read
   */
  public abstract Soundbank getSoundbank(InputStream stream)
    throws InvalidMidiDataException, IOException;
  
  /**
   * Get a Soundbank from the given File.
   * 
   * @param file from which to read the Soundbank
   * 
   * @return the Soundbank object
   * 
   * @throws InvalidMidiDataException if the data provided by File cannot be recognized
   * @throws IOException if the data provided by File cannot be read
   */
  public abstract Soundbank getSoundbank(File file)
    throws InvalidMidiDataException, IOException;
}
