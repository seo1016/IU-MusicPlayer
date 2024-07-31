package com.example.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import com.example.musicplayer.databinding.ActivityMainBinding
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var musicList: ArrayList<Int>
    private var musicIdx: Int = 0
    private lateinit var imageList: ArrayList<Int>
    private lateinit var titleList: ArrayList<String>
    private var pause = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        musicList = ArrayList()
        imageList = ArrayList()
        titleList = ArrayList()

        musicList.add(R.raw.shopper)
        musicList.add(R.raw.holssi)
        musicList.add(R.raw.love_wins_all)
        imageList.add(R.drawable.shopper)
        imageList.add(R.drawable.holssi)
        imageList.add(R.drawable.love_wins_all)
        titleList.add("Shopper")
        titleList.add("홀씨")
        titleList.add("Love wins all")

        binding.playArrow.setImageResource(R.drawable.play_arrow)
        binding.musicImage.setImageResource(imageList[musicIdx])
        binding.musicTitle.text = titleList[musicIdx]

        binding.playArrow.setOnClickListener {
            if (mediaPlayer == null) { //처음 재생했을 때
                firstPlayMediaPlayer()
            } else if (mediaPlayer!!.isPlaying) { //재생 중 정지했을 때
                pauseMediaPlayer()
            } else if (!mediaPlayer!!.isPlaying) { //정지 중 다시 재생했을 때
                resumeMediaPlayer()
            }
        }

        mediaPlayer?.setOnCompletionListener {
            if (musicIdx == musicList.size-1) {
                musicIdx = 0
            } else {
                musicIdx += 1
            }

            mediaPlayer?.reset()
            binding.seekbar.progress = 0
            mediaPlayer = MediaPlayer.create(this@MainActivity, musicList[musicIdx])
            mediaPlayer?.start()
            playClicked()
            binding.playArrow.setImageResource(R.drawable.pause)
            binding.musicImage.setImageResource(imageList[musicIdx])
            binding.musicTitle.text = titleList[musicIdx]
        }

        binding.skipNext.setOnClickListener {
            if (musicIdx == musicList.size-1) {
                musicIdx = 0
            } else {
                musicIdx += 1
            }
            mediaPlayer?.stop()

            binding.seekbar.progress = 0
            pause = 0
            mediaPlayer = MediaPlayer.create(this, musicList[musicIdx])
            mediaPlayer?.start()
            playClicked()
            binding.playArrow.setImageResource(R.drawable.pause)
            binding.musicImage.setImageResource(imageList[musicIdx])
            binding.musicTitle.text = titleList[musicIdx]
        }

        binding.skipPrevious.setOnClickListener {
            if (musicIdx == 0) {
                musicIdx = musicList.size-1
            } else {
                musicIdx -= 1
            }
            mediaPlayer?.stop()

            binding.seekbar.progress = 0
            pause = 0
            mediaPlayer = MediaPlayer.create(this, musicList[musicIdx])
            mediaPlayer?.start()
            playClicked()
            binding.playArrow.setImageResource(R.drawable.pause)
            binding.musicImage.setImageResource(imageList[musicIdx])
            binding.musicTitle.text = titleList[musicIdx]
        }
    }

    private fun firstPlayMediaPlayer() {
        mediaPlayer?.prepare()
        mediaPlayer = MediaPlayer.create(this, musicList[musicIdx])
        playClicked()
        binding.playArrow.setImageResource(R.drawable.pause)
    }

    private fun pauseMediaPlayer() {
        mediaPlayer?.pause()
        pause = mediaPlayer!!.currentPosition
        binding.playArrow.setImageResource(R.drawable.play_arrow)
    }

    private fun resumeMediaPlayer() {
        playClicked()
        binding.playArrow.setImageResource(R.drawable.pause)
    }

    private fun playClicked() {
        binding.seekbar.max = mediaPlayer!!.duration

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        mediaPlayer?.start()

        Thread {
            while (mediaPlayer!!.isPlaying) {
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                runOnUiThread {
                    binding.seekbar.progress = mediaPlayer!!.currentPosition
                }
            }
        }.start()
    }
}