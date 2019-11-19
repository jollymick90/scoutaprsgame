package arco.present.aprs.reader.common

import android.os.Environment
import arco.present.aprs.reader.MainActivity
import arco.present.aprs.reader.ui.MapFragment
import arco.present.aprs.reader.ui.ToolsFragment

val TAGLOG: String = "HIDE-SICK"

val MAX_WAV_SIZE: Long = 4294967295

val outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording-test.wav"
val recordingFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.wav"
val decodingFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/decoding.wav"
val decodingBaseFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/decoding_"
val ext = ".wav"

var counter = 0;

var gameCounter = 0;

var mainAct: MainActivity? = null;

var mapFragment: ToolsFragment? = null;