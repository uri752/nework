package ru.netology.nework.adapter

import android.widget.SeekBar

interface OnClick<T> {
    fun onClik(t: T)
    fun onRemove(t: T)
    fun onLike(t: T)
    fun onPlayMusic(t: T, seekBar: SeekBar)
    fun onPlayVideo(t: T)
    fun onPause()
    fun onShare(t: T)
    fun onImage(t: T)
}