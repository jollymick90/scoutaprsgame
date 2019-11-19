package arco.present.aprs.reader.decode

import android.os.AsyncTask
import android.util.Log
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.core.ax25.*
import arco.present.aprs.reader.core.sound.sampled.AudioInputStream
import arco.present.aprs.reader.core.sound.sampled.AudioSystem
import arco.present.aprs.reader.core.sound.sampled.UnsupportedAudioFileException
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


class DecoderTask(
    private val parserBeacon: ParserBeaconI,
    private val currentIndex: Int,
    private val aprsDecoder: AprsDecoderI
) : AsyncTask<File, Void, Boolean>() {
    val decode: DecodeAPRS = DecodeAPRS(parserBeacon);

    override fun doInBackground(vararg files: File): Boolean {
        try {
            Log.i(TAGLOG, "Starting decode")

            files?.let { it: Array<out File?> ->
                Log.i(TAGLOG, "Start decode $currentIndex")
                var file = it[0]
                decode.takeBeacon(file?.canonicalPath)
                return true;
            }
        } catch (e: Exception) {
            Log.e(TAGLOG, "error start decode $currentIndex", e)
            return false;
        }
    }

    override fun onPostExecute(result: Boolean?) {
        Log.i(TAGLOG, "END parse clear game")
        aprsDecoder.clearGame(currentIndex)
    }
}

class DecoderThread(
    private val parserBeacon: ParserBeaconI,
    private val currentIndex: Int,
    private val aprsDecoder: AprsDecoderI,
    private val file: File

): Runnable {
    val decode: DecodeAPRS = DecodeAPRS(parserBeacon);

    override fun run() {
        decode.takeBeacon(file?.canonicalPath)
        Log.i(TAGLOG, "Thread END parse clear game")
        aprsDecoder.clearGame(currentIndex)
    }

}

class DecodeAPRS(private val parserBeacon: ParserBeaconI) : PacketHandler {
    var beacon = ""
    private val packet_count: Int = 0
    private var sample_count: Long = 0
    private val last: ByteArray? = null
    private val last_sample_count: Long = 0
    private val dup_count: Int = 0
    fun incSampleCount() {
        sample_count++
    }


    override fun handlePacket(bytes: ByteArray) {
        beacon = Packet.format(bytes)
        //[IK3CYN>APRS,WIDE2-1:/054159h4525.99N/01211.60E-000/000/A=000000/Ti=35/V=7415 Trackuino reminder: replace callsign with your own]
        Log.i(TAGLOG, "Get Beacon:$beacon")
        parserBeacon.parseBeacon(beacon);

        return
    }

    fun takeBeacon(fin: String?) {
        //Audio rate is 48000, 2 channels, 2 bytes per frame, 8 bits per sample
        //Audio rate is 48000, 2 channels, 4 bytes per frame, 16 bits per sample

        val rate = 48000

        var mod: Afsk1200Modulator? = null
        var multi: PacketDemodulator? = null
        try {
            multi = Afsk1200MultiDemodulator(rate, this)
            mod = Afsk1200Modulator(rate)
        } catch (e: Exception) {
            Log.e(TAGLOG, "Exception trying to create an Afsk1200 object: " + e.message)
            return

        }

        /*** process an input sound file  */


        if (fin != null) {
            //			System.out.printf("Trying to decode packets from <%s>\n",fin);
            var ios: AudioInputStream? = null
            try {
                Log.i(TAGLOG, "Get File$fin")
                ios = AudioSystem.getAudioInputStream(File(fin))

            } catch (ioe: IOException) {
                Log.e(TAGLOG, "IO Error: " + ioe.message)
                return
            } catch (usafe: UnsupportedAudioFileException) {
                Log.e(TAGLOG, "Audio file format not supported: ", usafe)
                return
            }

            Log.i(TAGLOG, "Success audio input stream")
            val fmt = ios!!.format
            Log.i(TAGLOG, "Audio rate is " + fmt.sampleRate + ", Channels: " + fmt.channels + ", Frame Rate:" + fmt.frameSize + ". Sample size bits:" + fmt.sampleSizeInBits + " bits per sample")

            var decimation = 1
            val d = fmt.sampleRate / rate
            if (Math.abs(Math.round(d) - d) / d < 0.01)
                decimation = Math.round(d).toInt()
            else {
                Log.e(TAGLOG, "Sample rates must match or lead to decimation by an integer!\n")
                return
            }

            val raw = ByteArray(fmt.frameSize)
            val f = FloatArray(1)
            val bb: ByteBuffer
            if (fmt.isBigEndian)
                bb = ByteBuffer.wrap(raw).order(ByteOrder.BIG_ENDIAN)
            else
                bb = ByteBuffer.wrap(raw).order(ByteOrder.LITTLE_ENDIAN)
            var j = 0
            var k = 0
            var scale: Float = 0.0f

            when (fmt.sampleSizeInBits) {
                32 -> scale = 1.0f / (fmt.channels.toFloat() * 2147483648.0f)
                16 -> scale = 1.0f / (fmt.channels.toFloat() * 32768.0f)
                8 -> scale = 1.0f / (fmt.channels.toFloat() * 256.0f)
            }
            //System.out.printf("Format bits per sample = %d\n",fmt.getSampleSizeInBits());

            var kk = 0
            while (true) {
                if (kk > 46150 && kk < 46160) {
                    if (k == 46157)
                        Log.i(TAGLOG, "KK: $kk")
                }
                kk++
                try {
                    val n = ios!!.read(raw)
                    //					System.out.println("n: "+n);
                    if (n != raw.size) {
                        Log.i(TAGLOG, "Done!?!\n")
                        return
                    }
                    bb.rewind()
                    f[0] = 0.0f
                    // we average over channels (stereo)
                    for (i in 0 until fmt.channels) {
                        when (fmt.sampleSizeInBits) {
                            32 -> f[0] += bb.int.toFloat()
                            16 -> f[0] += bb.short.toFloat()
                            8 -> f[0] += bb.get().toFloat()
                            else -> {
                                Log.e(TAGLOG, "Can't process files with " + fmt.sampleSizeInBits + " bits per sample")
                                return
                            }
                        }
                    }
                    f[0] = scale * f[0]
                    if (j == 0) {
                        this.incSampleCount()
                        multi!!.addSamples(f, 1)
                        k++
                        if (k == rate) {
                            //							System.out.printf("peak level %d\n",multi.peak());
                            k = 0
                        }
                    }
                    j++
                    if (j == decimation) j = 0
                } catch (eofe: EOFException) {
                    Log.i(TAGLOG, "Done!")
                } catch (e: IOException) {
                    Log.e(TAGLOG, "IO Error while reading audio: " + e.message)
                }

            }
        }

    }
}