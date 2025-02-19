package org.lidraughts.plugin

import android.media.AudioAttributes
import android.media.SoundPool
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "SoundEffect")
class SoundEffectPlugin : Plugin() {

  private val soundPool = SoundPool.Builder()
          .setMaxStreams(3)
          .setAudioAttributes(
                  AudioAttributes.Builder()
                          .setUsage(AudioAttributes.USAGE_GAME)
                          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                          .build()
          ).build()

  private val audioMap = HashMap<String, Int>()

  @PluginMethod
  fun loadSound(call: PluginCall) {
    val audioId = call.getString("id")
    val path = call.getString("path")

    if (audioId === null) {
      call.reject("Must supply an id")
      return
    }
    if (path === null) {
      call.reject("Must supply a path")
      return
    }

    val afd = this.activity.applicationContext.resources.assets.openFd("public/" + path)

    audioMap[audioId] = soundPool.load(afd, 1)

    call.resolve()
  }

  @PluginMethod
  fun play(call: PluginCall) {
    val audioId = call.getString("id")
    if (audioId === null) {
      call.reject("Must supply an id")
      return
    }

    val aid = audioMap[audioId]
    if (aid === null) {
      call.reject("Audio asset not found")
      return
    }

    soundPool.play(aid, 1f, 1f, 1, 0, 1f)
    call.resolve()
  }
}
