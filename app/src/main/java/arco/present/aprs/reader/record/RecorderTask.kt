package arco.present.aprs.reader.record

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationCompat
import arco.present.aprs.reader.common.MAX_WAV_SIZE
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.ui.PrintInfo
import java.io.*
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.RandomAccess

class RecorderTask : AsyncTask<File, Void, Boolean> {

    companion object {
        val AUDIO_SOURCE: Int = MediaRecorder.AudioSource.MIC
        val SAMPLE_RATE: Int = 48000 //hz
        val ENCODING: Int = AudioFormat.ENCODING_PCM_16BIT
        val CHANNEL_MASK: Int = AudioFormat.CHANNEL_IN_STEREO
        val BUFFER_SIZE = 2 * AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING)

    }

    private var printInfo: PrintInfo? = null;
    private var ctx: Context? = null

    constructor(ctx: Context, printInfo: PrintInfo): super(){
        setContext(ctx)
        this.printInfo = printInfo;
    }

    private fun setContext(ctx: Context) {
        this.ctx = ctx
    }

    override fun doInBackground(vararg files: File?): Boolean {

        var audioRecord: AudioRecord? = null;
        var wavOut: FileOutputStream? = null;
        var startTime:Long = 0
        var endTime: Long = 0

        var file: File? = files[0]

        try {


            var pathFile = file?.absolutePath
            audioRecord = AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE)
            wavOut = FileOutputStream(file)

            //Write out the wav file header
            writeWavHeader(wavOut, CHANNEL_MASK, SAMPLE_RATE, ENCODING)

            //Avoid loop allocations

            var buffer: ByteArray = ByteArray(BUFFER_SIZE)
            var run = true;
            var read: Int = 0;
            var total: Long = 0;

            startTime = SystemClock.elapsedRealtime()
            audioRecord.startRecording()
            Log.i(TAGLOG,"Start record $pathFile")
            printInfo?.printInfo("Start record $pathFile")
            while(run && !isCancelled) {
                read = audioRecord.read(buffer, 0, buffer.size)

                if (total + read > MAX_WAV_SIZE){
                    var i = 0
                    while (i < read && total <= 4294967295L) {
                        wavOut.write(buffer[i].toInt())
                        i++
                        total++
                    }

                    run = false;


                } else {
                    wavOut.write(buffer, 0, read)
                    total += read
                }
            }


        } catch (e: Exception) {

            Log.e(TAGLOG, "Error to recorder task background: ", e)
            return false;
        } finally {
            if (audioRecord != null) {
                try {
                    if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING)
                    {
                        Log.i(TAGLOG,"STOP record")
                        printInfo?.printInfo("Stop record");

                        audioRecord.stop()
                        endTime = SystemClock.elapsedRealtime()
                    }
                } catch (e: IllegalStateException) {
                    Log.e(TAGLOG, "Error to stop record", e)
                }
                if (audioRecord.state == AudioRecord.STATE_INITIALIZED)
                    audioRecord.release()
            }
            if (wavOut != null) {
                try {
                    wavOut.close()
                } catch (ex: IOException) {
                    Log.e(TAGLOG, "Error to close record", ex)

                }
            }
            Log.i(TAGLOG,"STOP record")
        }

        try {
            // This is not put in the try/catch/finally above since it needs to run
            // after we close the FileOutputStream
            files[0]?.let { it: File ->

                updateWavHeader(it)
                Log.e(TAGLOG, "success update wav header")

                return true
            } ?:
                return false

        } catch (ex: IOException) {
            Log.e(TAGLOG, "Error to close record", ex)

            return false
        }


        return true
    }

    fun updateWavHeader(wav: File) {
        val sizes:ByteArray = ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt((wav.length() - 8).toInt())
            .putInt((wav.length() - 44).toInt())
            .array()

        var accessWave:RandomAccessFile? =  null;

        try {
            accessWave = RandomAccessFile(wav, "rw")
            // ChunkSize
            accessWave.seek(4)
            accessWave.write(sizes, 0, 4)

            // Subchunk2Size
            accessWave.seek(40)
            accessWave.write(sizes, 4, 4)
        } catch (ex: IOException) {
            // Rethrow but we still close accessWave in our finally
            throw ex
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close()
                } catch (ex: IOException) {
                    //
                }

            }
        }

    }

    fun writeWavHeader(out: OutputStream, channelMask: Int, sampleRate: Int, encoding: Int) {

        val channels: Short
        when (channelMask) {
            AudioFormat.CHANNEL_IN_MONO -> channels = 1
            AudioFormat.CHANNEL_IN_STEREO -> channels = 2
            else -> throw IllegalArgumentException("Unacceptable channel mask")
        }

        val bitDept: Short
        when (encoding) {
            AudioFormat.ENCODING_PCM_8BIT -> bitDept = 8
            AudioFormat.ENCODING_PCM_16BIT -> bitDept = 16
            AudioFormat.ENCODING_PCM_FLOAT -> bitDept = 32
            else -> throw java.lang.IllegalArgumentException("Unacceptable encoding")
        }

        val littleBytes: ByteArray = ByteBuffer
            .allocate(14)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putShort(channels)
            .putInt(sampleRate)
            .putInt(sampleRate * channels * (bitDept / 8))
            .putShort((channels * (bitDept / 8)).toShort())
            .putShort(bitDept)
            .array()

        out.write(
            byteArrayOf(
                'R'.toByte(),'I'.toByte(), 'F'.toByte(), 'F'.toByte(), //RIFF Header
                0,0,0,0,// Chunk Size (must be updated later)
                'W'.toByte(),'A'.toByte(),'V'.toByte(),'E'.toByte(),
                'f'.toByte(),'m'.toByte(), 't'.toByte(), //FMT sub chunk
                16,0,0,0, // sub chunk 1 size
                1,0, //Audio format
                littleBytes[0], littleBytes[1], //NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample
                // data subchunk
                'd'.toByte(), 'a'.toByte(), 't'.toByte(), 'a'.toByte(), // Subchunk2ID
                0, 0, 0, 0 // Subchunk2Size (must be updated later)
            )
        )
    }

    fun stopTask() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}