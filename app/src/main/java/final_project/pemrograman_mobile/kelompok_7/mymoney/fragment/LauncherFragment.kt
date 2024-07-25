package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentLauncherBinding

class LauncherFragment : Fragment() {
	private lateinit var frontend: FragmentLauncherBinding;

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		frontend = FragmentLauncherBinding.inflate(inflater, container, false)
		return frontend.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val animator_scalex = ObjectAnimator.ofFloat(frontend.logoLauncher, "scaleX", 0.75F, 1.25F).apply {
			duration = 2000
			interpolator = BounceInterpolator()
			repeatMode = ValueAnimator.REVERSE
			repeatCount = ValueAnimator.INFINITE
		}
		val animator_scaley = ObjectAnimator.ofFloat(frontend.logoLauncher, "scaleY", 0.75F, 1.25F).apply {
			duration = 2000
			interpolator = BounceInterpolator()
			repeatMode = ValueAnimator.REVERSE
			repeatCount = ValueAnimator.INFINITE
		}
		AnimatorSet().apply {
			playTogether(animator_scalex, animator_scaley)
			start()
		}
	}
}