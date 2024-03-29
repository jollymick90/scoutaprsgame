/* Mixer API
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


package arco.present.aprs.reader.core.sound.sampled.spi;

import arco.present.aprs.reader.core.sound.sampled.Mixer;

/**
 * This abstract class defines an interface to mixer providers.
 * Concrete subclasses will implement the methods in this class.
 * @since 1.3
 */
public abstract class MixerProvider
{
  /**
   * Create a new mixer provider.
   */
  public MixerProvider()
  {
  }

  /**
   * Return a mixer that matches the given info object.
   * @param info description of the mixer to match
   * @return the mixer
   * @throws IllegalArgumentException if no mixer matches the description
   */
  public abstract Mixer getMixer(Mixer.Info info);

  /**
   * Return an array of info objects describing all the mixers provided by
   * this provider.
   */
  public abstract Mixer.Info[] getMixerInfo();

  /**
   * Return true if a mixer matching the provided description is supported.
   * @param info description of the mixer to match
   * @return true if it is supported by this provider
   */
  public boolean isMixerSupported(Mixer.Info info)
  {
    Mixer.Info[] infos = getMixerInfo();
    for (int i = 0; i < infos.length; ++i)
      {
	if (info.equals(infos[i]))
	  return true;
      }
    return false;
  }
}
