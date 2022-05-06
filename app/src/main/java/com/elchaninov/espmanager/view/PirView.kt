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
import androidx.core.animation.doOnEnd
import androidx.core.widget.ImageViewCompat
import com.elchaninov.espmanager.R
import com.elchaninov.espmanager.databinding.PirViewBinding
import kotlin.properties.Delegates

enum class PirState { DISABLE, MONITORING, ALARM }

class PirView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, R.style.DefaultPirStyle
    )

    constructor(context: Context, attrs: AttributeSet?) : this(
        context, attrs, R.attr.pirStyle
    )

    constructor(context: Context) : this(context, null)

    private val binding: PirViewBinding

    private var pirDisableColor by Delegates.notNull<Int>()
    private var pirMonitoringColor by Delegates.notNull<Int>()
    private var pirAlarmColor by Delegates.notNull<Int>()

    private var scaleMinDisableState by Delegates.notNull<Float>()
    private var scaleMaxDisableState by Delegates.notNull<Float>()
    private var scaleMinMonitoringState by Delegates.notNull<Float>()
    private var scaleMaxMonitoringState by Delegates.notNull<Float>()
    private var scaleMinAlarmState by Delegates.notNull<Float>()
    private var scaleMaxAlarmState by Delegates.notNull<Float>()

    private var durationPulseDisableState by Delegates.notNull<Long>()
    private var durationPulseMonitoringState by Delegates.notNull<Long>()
    private var durationPulseAlarmState by Delegates.notNull<Long>()

    private var animator: ObjectAnimator? = null

    var pirState: PirState = PirState.DISABLE
        set(value) {
            if (field != value) {
                field = value
                when (value) {
                    PirState.DISABLE -> {
                        setColorImage(pirDisableColor)
                        setAnimate()
                    }
                    PirState.MONITORING -> {
                        setColorImage(pirMonitoringColor)
                        setAnimate()
                    }
                    PirState.ALARM -> {
                        setColorImage(pirAlarmColor)
                        setAnimate()
                    }
                }
            }
        }

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.pir_view, this, true)
        binding = PirViewBinding.bind(this)
        initializeAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initializeAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PirView, defStyleAttr, defStyleRes)

        pirAlarmColor = typedArray.getColor(R.styleable.PirView_pirAlarmColor, Color.RED)
        pirMonitoringColor =
            typedArray.getColor(R.styleable.PirView_pirMonitoringColor, Color.GREEN)
        pirDisableColor = typedArray.getColor(R.styleable.PirView_pirDisableColor, Color.GRAY)

        scaleMinDisableState = typedArray.getFloat(R.styleable.PirView_scaleMinDisableState, 0.5f)
        scaleMaxDisableState = typedArray.getFloat(R.styleable.PirView_scaleMaxDisableState, 0.5f)

        scaleMinMonitoringState =
            typedArray.getFloat(R.styleable.PirView_scaleMinMonitoringState, 0.75f)
        scaleMaxMonitoringState =
            typedArray.getFloat(R.styleable.PirView_scaleMaxMonitoringState, 0.85f)

        scaleMinAlarmState = typedArray.getFloat(R.styleable.PirView_scaleMinAlarmState, 0.85f)
        scaleMaxAlarmState = typedArray.getFloat(R.styleable.PirView_scaleMaxAlarmState, 1.0f)


        durationPulseDisableState =
            typedArray.getInt(R.styleable.PirView_durationPulseDisableState, 1600).toLong()
        durationPulseMonitoringState =
            typedArray.getInt(R.styleable.PirView_durationPulseMonitoringState, 1600).toLong()
        durationPulseAlarmState =
            typedArray.getInt(R.styleable.PirView_durationPulseAlarmState, 250).toLong()

        pirState = PirState.values()[typedArray.getInt(R.styleable.PirView_pirState, 0)]

        typedArray.recycle()

        setColorImage(pirDisableColor)
        setAnimate()
    }

    private fun setColorImage(color: Int) {
        ImageViewCompat.setImageTintList(binding.image, ColorStateList.valueOf(color))
    }

    private fun setAnimate() {
        when (pirState) {
            PirState.DISABLE -> {
                animator?.cancel()
                setAnimateImage(
                    scaleMinDisableState,
                    scaleMaxDisableState,
                    durationPulseDisableState
                )
            }
            PirState.MONITORING -> {
                animator?.cancel()
                setAnimateImage(
                    scaleMinMonitoringState,
                    scaleMaxMonitoringState,
                    durationPulseMonitoringState
                )
            }
            PirState.ALARM -> {
                animator?.cancel()
                setAnimateImage(
                    scaleMinAlarmState,
                    scaleMaxAlarmState,
                    durationPulseAlarmState
                )
            }
        }
    }

    private fun setAnimateImage(valueStart: Float, valueEnd: Float, duration: Long) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, valueEnd)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, valueEnd)
        animator =
            ObjectAnimator.ofPropertyValuesHolder(binding.image, scaleX, scaleY)
        animator?.let {
            it.interpolator = AccelerateDecelerateInterpolator()
            it.duration = duration
            it.doOnEnd {
                if (valueStart != valueEnd)
                    setEndlessPulseAnimation(valueEnd, valueStart, duration)
            }
            it.start()
        }
    }


    private fun setEndlessPulseAnimation(valueStart: Float, valueEnd: Float, duration: Long) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, valueStart, valueEnd)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, valueStart, valueEnd)
        animator = ObjectAnimator.ofPropertyValuesHolder(binding.image, scaleX, scaleY)
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