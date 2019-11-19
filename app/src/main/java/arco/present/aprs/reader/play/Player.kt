package arco.present.aprs.reader.play

import android.media.MediaPlayer
import android.util.Log
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.common.counter
import arco.present.aprs.reader.common.outputFile
import arco.present.aprs.reader.common.recordingFile

class AprsPlayer {
    init {
        Log.i(TAGLOG, "Init player ${++counter}")
    }
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    companion object {
        fun newInstance(): AprsPlayer {

            return AprsPlayer();
        }
    }
    fun play() {

        try {
            //stopPlayer()
            mediaPlayer.setDataSource(recordingFile)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error play rec:$recordingFile", e)
        }
    }

    fun stopPlayer() {
        try {
          if( mediaPlayer.isPlaying) {
              mediaPlayer.stop()
              mediaPlayer.release()

          }
        } catch (e: Exception) {
            Log.e(TAGLOG, "Error play rec:$recordingFile", e)
        }
    }
}