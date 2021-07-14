package library.extensions

import android.animation.ValueAnimator
import android.transition.Fade
import android.transition.Slide
import android.transition.Transition
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation

class AdvAnim private constructor(){
    companion object{
        const val DIRECTION_CLOCKWISE = 5
        const val DIRECTION_UP_DOWN = 0
        private const val DURATION_TRANSITION_DEFAULT: Long = 300
        private const val DURATION_TRANSITION_DEFAULT_SLOW: Long = 700

        fun expandWidthAnimation(v: ViewGroup) {
            val targetWidth: Int = v.width
            val params: ViewGroup.LayoutParams = v.layoutParams
            params.width = 1
            v.layoutParams = params
            v.visibility = View.VISIBLE
            val anim: ValueAnimator = ValueAnimator.ofInt(1, targetWidth)
            anim.addUpdateListener { valueAnimator ->
                val width = valueAnimator.animatedValue as Int
                val p = v.layoutParams
                p.width = width
                v.layoutParams = p
            }
            anim.duration = 1000
            anim.start()
        }

        val rotate: Animation
            get() {
                val rotate = RotateAnimation(360.0f, 0.0f, 1, 0.5f, 1, 0.5f)
                rotate.repeatCount = -1
                rotate.duration = 2000
                rotate.interpolator = LinearInterpolator()
                return rotate
            }

        fun getRotate(direction: Int): Animation {
            val rotate = if (direction == DIRECTION_CLOCKWISE) {
                RotateAnimation(0.0f, 360.0f, 1, 0.5f, 1, 0.5f)
            } else {
                RotateAnimation(360.0f, 0.0f, 1, 0.5f, 1, 0.5f)
            }
            rotate.repeatCount = -1
            rotate.duration = 2000
            rotate.interpolator = LinearInterpolator()
            return rotate
        }

        fun getRotateOnce(direction: DirectionType, fromAngle: Float, toAngle: Float): Animation {
            val rotate = if (direction == DirectionType.DIRECTION_CLOCKWISE) {
                RotateAnimation(fromAngle, toAngle, 1, 0.5f, 1, 0.5f)
            } else {
                RotateAnimation(fromAngle, toAngle, 1, 0.5f, 1, 0.5f)
            }
            rotate.duration = 500
            rotate.interpolator = LinearInterpolator()
            return rotate
        }

        fun getSlideTransition(gravity: Int, fast: Boolean): Transition {
            val dur = if (fast) {
                DURATION_TRANSITION_DEFAULT
            } else {
                DURATION_TRANSITION_DEFAULT_SLOW
            }
            val slide = Slide(gravity)
            slide.duration = dur
            return slide
        }

        fun getFadeTransition(fade_mode: Int, fast: Boolean): Transition {
            val dur = if (fast) {
                DURATION_TRANSITION_DEFAULT
            } else {
                DURATION_TRANSITION_DEFAULT_SLOW
            }
            val fade = Fade(fade_mode)
            fade.duration = dur
            return fade
        }

        fun getUpUpAnimation(fast: Boolean): Animation {
            val mAnimation: Animation = TranslateAnimation(0, 0.0f, 0, 0.0f, 1, 0.0f, 1, -1.0f)
            if (fast) {
                mAnimation.duration = 800
            } else {
                mAnimation.duration = 1000
            }
            mAnimation.repeatCount = -1
            mAnimation.interpolator = LinearInterpolator()
            return mAnimation
        }

        fun changeVisibility(
            view: View,
            newVisibility: Visibility,
            duration: Duration,
            listener: (Int,Int)->Unit
        ) {
            val dur =
                if (duration == Duration.FAST) DURATION_TRANSITION_DEFAULT else DURATION_TRANSITION_DEFAULT_SLOW

            if (newVisibility == Visibility.VISIBLE) {
                view.visibility = View.VISIBLE
                view.measure(-1, -2)
                val height = view.measuredHeight
                val anim: ValueAnimator = ValueAnimator.ofInt(*intArrayOf(0, height / 2, height))
                anim.addUpdateListener { valueAnimator ->
                    val params = valueAnimator.animatedValue as Int
                    view.layoutParams.height = params
                    if (params == 0) {
                        view.visibility = View.VISIBLE
                    }
                    view.requestLayout()
                    listener.invoke(
                        Integer.valueOf(height),
                        Integer.valueOf(params)
                    )
                }
                anim.duration = dur
                anim.start()
            } else if (newVisibility == Visibility.GONE) {
                view.measure(-1, -2)
                val height = view.measuredHeight
                val anim: ValueAnimator = ValueAnimator.ofInt(height, height / 2, 0)
                anim.addUpdateListener { valueAnimator ->
                    val currentHeight = valueAnimator.animatedValue as Int
                    view.layoutParams.height = currentHeight
                    view.requestLayout()
                    if (currentHeight == 0) {
                        view.visibility = View.GONE
                    }
                }
                anim.duration = dur
                anim.start()
            }
        }

    }
}


enum class DirectionType {
    DIRECTION_CLOCKWISE, DIRECTION_ANTI_CLOCKWISE, DIRECTION_DOWN
}

enum class Duration {
    FAST, SLOW
}

enum class Visibility {
    VISIBLE, GONE
}