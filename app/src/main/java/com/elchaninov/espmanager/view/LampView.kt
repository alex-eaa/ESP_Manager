package com.elchaninov.espmanager.view

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.widget.ImageViewCompat
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.LampViewBinding
import kotlin.properties.Delegates

class LampView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, 0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context, attrs, R.attr.defaultLampStyle
    )

    constructor(context: Context) : this(context, null)

    private val binding: LampViewBinding

    private var lampOffColor by Delegates.notNull<Int>()
    private var lampOnColor by Delegates.notNull<Int>()
    private var raysColor by Delegates.notNull<Int>()

    private var scaleMinRays by Delegates.notNull<Float>()
    private var scaleMaxRays by Delegates.notNull<Float>()

    private var durationPulseRays by Delegates.notNull<Long>()
    private var durationOnOffLamp by Delegates.notNull<Long>()

    private var scaleOffRays = 0.5f

    private var animator: ObjectAnimator? = null

    var lampState: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                when (value) {
                    false -> lampTurnsOff()
                    true -> lampTurnsOn()
                }
            }
        }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.lamp_view, this, true)
        binding = LampViewBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.LampView, defStyleAttr, defStyleRes)

        lampOffColor = typedArray.getColor(R.styleable.LampView_lampOffColor, Color.GRAY)
        lampOnColor = typedArray.getColor(R.styleable.LampView_lampOnColor, Color.YELLOW)
        raysColor = typedArray.getColor(R.styleable.LampView_raysColor, Color.YELLOW)

        scaleMinRays = typedArray.getFloat(R.styleable.LampView_scaleMinRays, 0.9f)
        scaleMaxRays = typedArray.getFloat(R.styleable.LampView_scaleMaxRays, 1.1f)

        durationPulseRays =
            typedArray.getInt(R.styleable.LampView_durationPulseRays, 500).toLong()
        durationOnOffLamp =
            typedArray.getInt(R.styleable.LampView_durationOnOffLamp, 250).toLong()

        setColorImage(binding.imageLampOff, lampOffColor)
        setColorImage(binding.imageLampOn, lampOnColor)
        setColorImage(binding.imageRays, raysColor)
        binding.imageRays.scaleX = scaleOffRays
        binding.imageRays.scaleY = scaleOffRays

        lampState = typedArray.getBoolean(R.styleable.LampView_lampState, false)

        typedArray.recycle()
    }

    private fun setColorImage(imageView: ImageView, color: Int) {
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(color))
    }

    private fun lampTurnsOff() {
        animator?.cancel()

        val valueAnimator = ValueAnimator.ofFloat(1f, 0f)
        valueAnimator.duration = durationOnOffLamp
        valueAnimator.addUpdateListener { animation ->
            binding.imageLampOn.alpha = animation.animatedValue as Float
            binding.imageRays.alpha = animation.animatedValue as Float
        }
        valueAnimator.doOnEnd {
            binding.imageLampOn.alpha = 0.0f
            binding.imageRays.alpha = 0.0f
            binding.imageRays.scaleX = scaleOffRays
            binding.imageRays.scaleY = scaleOffRays
        }
        valueAnimator.start()
    }

    private fun lampTurnsOn() {
        animator?.cancel()

        val valueAnimator = ValueAnimator.ofFloat(1f)
        valueAnimator.duration = durationOnOffLamp
        valueAnimator.addUpdateListener { animation ->
            binding.imageLampOn.alpha = animation.animatedValue as Float
            binding.imageRays.alpha = animation.animatedValue as Float
        }
        valueAnimator.doOnEnd {
            raysTurnOn()
        }
        valueAnimator.start()
    }


    private fun raysTurnOn() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0.6f, scaleMaxRays)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.6f, scaleMaxRays)

        val valueAnimator = ValueAnimator.ofPropertyValuesHolder(scaleX, scaleY)
        valueAnimator.duration = durationPulseRays
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            binding.imageRays.scaleX = animation.animatedValue as Float
            binding.imageRays.scaleY = animation.animatedValue as Float
        }
        valueAnimator.doOnEnd {
            if (scaleMinRays != scaleMaxRays)
                setEndlessPulseAnimation(scaleMaxRays, scaleMinRays, durationPulseRays)
        }
        valueAnimator.start()
    }

    private fun setEndlessPulseAnimation(valueStart: Float, valueEnd: Float, duration: Long) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, valueStart, valueEnd)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, valueStart, valueEnd)
        animator = ObjectAnimator.ofPropertyValuesHolder(binding.imageRays, scaleX, scaleY)
        animator?.let {
            it.interpolator = AccelerateDecelerateInterpolator()
            it.duration = duration
            it.repeatCount = ValueAnimator.INFINITE
            it.repeatMode = ValueAnimator.REVERSE
            it.setAutoCancel(true)
            it.start()
        }
    }
}