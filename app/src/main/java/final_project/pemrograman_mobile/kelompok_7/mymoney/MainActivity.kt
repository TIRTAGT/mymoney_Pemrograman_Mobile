package final_project.pemrograman_mobile.kelompok_7.mymoney

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.ActivityMainBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.asset.AssetFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.HomeFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.LauncherFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more.MoreFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.StatsFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more.PINFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class MainActivity : AppCompatActivity() {
	private lateinit var frontend: ActivityMainBinding
	private val fragmentInstance: HashMap<String, Fragment> = HashMap()
	private val fragmentStack: ArrayList<String> = ArrayList()
	private var currentlyActiveMenu: String? = null
	private lateinit var database: MonefyDatabase
	private val mainEventListeners: ArrayList<MainActivityEvent> = ArrayList()
	private var applicationPin = ""
	private lateinit var PINManager: PINFragment

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		frontend = ActivityMainBinding.inflate(layoutInflater, )
		setContentView(frontend.root)

		ViewCompat.setOnApplyWindowInsetsListener(frontend.main) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		switchFragment(LauncherFragment::class, 0)

		MainScope().launch(Dispatchers.IO) {
			preloadPINAuthenticationFragment()
		}

		this.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				onBackButtonPressed()
			}
		})

		onViewCreated()
	}

	@MainThread
	fun onViewCreated() {
		frontend.bottomNavbarTransaction.setOnClickListener { switchFragment(HomeFragment::class) }
		frontend.bottomNavbarStats.setOnClickListener { switchFragment(StatsFragment::class) }
		frontend.bottomNavbarAsset.setOnClickListener { switchFragment(AssetFragment::class) }
		frontend.bottomNavbarMore.setOnClickListener { switchFragment(MoreFragment::class) }
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()
	}

	fun onBackButtonPressed(ignoreFragmentProtection: Boolean = false) {
		if (!ignoreFragmentProtection && this.mainEventListeners.isNotEmpty()) {

			for (listener in this.mainEventListeners) {
				if (!listener.onBackPressTriggered()) { return }
			}

			this.onBackButtonPressed(true)
			return
		}

		this.fragmentStack.removeLast()

		if (supportFragmentManager.backStackEntryCount == 1) {
			finish()
			return
		}

		supportFragmentManager.popBackStackImmediate()
		this.currentlyActiveMenu = this.fragmentStack.last()
		handleBottomNavBarColor()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onStop() {
		super.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
	}

	//#region a
	@WorkerThread
	private suspend fun preloadHomeFragment() {
		database = MonefyDatabase.initialize(this.applicationContext)

		val home = HomeFragment()
		fragmentInstance[home.javaClass.name] = home

		delay(1000L)

		MainScope().launch(Dispatchers.Main) {
			switchFragment(home)
			frontend.bottomNavbar.visibility = View.VISIBLE
		}
	}

	@WorkerThread
	private suspend fun preloadPINAuthenticationFragment() {
		database = MonefyDatabase.initialize(this.applicationContext)

		val settings = database.appSettings().select()
		applicationPin = settings.pin

		if (applicationPin.isEmpty()) {
			preloadHomeFragment()
			return
		}

		PINManager = PINFragment()
		PINManager.setListener(object: PINFragment.OnPINFragmentResponse {
			override fun run(pin: String) {
				handleAuthenticationRequest(pin)
			}
		})
		fragmentInstance[PINManager.javaClass.name] = PINManager

		delay(500L)

		MainScope().launch(Dispatchers.Main) {
			switchFragment(PINManager)
		}
	}

	private fun handleAuthenticationRequest(pin: String) {
		if (pin == applicationPin) {
			MainScope().launch(Dispatchers.IO) {
				preloadHomeFragment()
			}

			return
		}

		PINManager.triggerInvalidPINMessage()
	}

	@MainThread
	fun switchFragment(a: Fragment, level: Int = 0) {
		switchFragment(a, level, null)
	}

	@MainThread
	fun switchFragment(a: KClass<out Fragment>, level: Int = 0) {
		val b: Fragment = a.createInstance()
		switchFragment(b, level, null)
	}

	@MainThread
	fun switchFragment(a: KClass<out Fragment>, level: Int = 0, data: Bundle?) {
		val b: Fragment = a.createInstance()
		switchFragment(b, level, data)
	}

	@MainThread
	fun switchFragment(targetFragment: Fragment, level: Int = 0, data: Bundle?) {
		val targetFragmentName: String = targetFragment::class.qualifiedName ?: return
		var previousFragment: String? = null

		if (this.fragmentStack.size != 0 && level < this.fragmentStack.size) {
			previousFragment = this.fragmentStack[level]
		}

		// Prevent switching to the same fragment
		if (targetFragmentName == previousFragment) { return }

		// Reset color for the bottom navbar
		when (this.currentlyActiveMenu) {
			HomeFragment::class.qualifiedName -> {
				frontend.bottomNavbarTransactionImage.setColorFilter(getColor(R.color.bottom_navbar_inactive_menu))
				frontend.bottomNavbarTransactionText.setTextColor(getColor(R.color.bottom_navbar_inactive_menu))
			}
			StatsFragment::class.qualifiedName -> {
				frontend.bottomNavbarStatsImage.setColorFilter(getColor(R.color.bottom_navbar_inactive_menu))
				frontend.bottomNavbarStatsText.setTextColor(getColor(R.color.bottom_navbar_inactive_menu))
			}
			AssetFragment::class.qualifiedName -> {
				frontend.bottomNavbarAssetImage.setColorFilter(getColor(R.color.bottom_navbar_inactive_menu))
				frontend.bottomNavbarAssetText.setTextColor(getColor(R.color.bottom_navbar_inactive_menu))
			}
			MoreFragment::class.qualifiedName -> {
				frontend.bottomNavbarMoreImage.setColorFilter(getColor(R.color.bottom_navbar_inactive_menu))
				frontend.bottomNavbarMoreText.setTextColor(getColor(R.color.bottom_navbar_inactive_menu))
			}
		}

		this.currentlyActiveMenu = targetFragmentName

		if (previousFragment != null) {
			supportFragmentManager.popBackStackImmediate()
			this.fragmentStack[level] = targetFragmentName
		}
		else {
			this.fragmentStack.add(targetFragmentName)
		}

		// Apply color for the bottom navbar
		handleBottomNavBarColor()

		targetFragment.arguments = data

		supportFragmentManager.commit {
			addToBackStack(targetFragmentName)

			replace(R.id.MainFragmentContainer, targetFragment)
		}
	}

	@MainThread
	private fun handleBottomNavBarColor() {
		this.currentlyActiveMenu?.let { Log.w("handleBottomNavBarColor()", it) }
		this.currentlyActiveMenu?.let { Log.w("handleBottomNavBarColor()", MoreFragment::class.qualifiedName!!) }
		this.currentlyActiveMenu?.let { Log.w("handleBottomNavBarColor()", StatsFragment::class.qualifiedName!!) }

		when (this.currentlyActiveMenu) {
			HomeFragment::class.qualifiedName -> {
				frontend.bottomNavbarTransactionImage.setColorFilter(getColor(R.color.bottom_navbar_active_menu))
				frontend.bottomNavbarTransactionText.setTextColor(getColor(R.color.bottom_navbar_active_menu))
			}
			StatsFragment::class.qualifiedName -> {
				frontend.bottomNavbarStatsImage.setColorFilter(getColor(R.color.bottom_navbar_active_menu))
				frontend.bottomNavbarStatsText.setTextColor(getColor(R.color.bottom_navbar_active_menu))
			}
			AssetFragment::class.qualifiedName -> {
				frontend.bottomNavbarAssetImage.setColorFilter(getColor(R.color.bottom_navbar_active_menu))
				frontend.bottomNavbarAssetText.setTextColor(getColor(R.color.bottom_navbar_active_menu))
			}
			MoreFragment::class.qualifiedName -> {
				frontend.bottomNavbarMoreImage.setColorFilter(getColor(R.color.bottom_navbar_active_menu))
				frontend.bottomNavbarMoreText.setTextColor(getColor(R.color.bottom_navbar_active_menu))
			}
		}
	}

	@MainThread
	fun setBottomNavVisibility(visibility: Boolean) {
		this.frontend.bottomNavbar.visibility = if(visibility) View.VISIBLE else View.GONE
	}

	fun addMainEventListener(listener: MainActivityEvent) {
		this.mainEventListeners.add(listener)
	}

	fun removeMainEventListener(listener: MainActivityEvent) {
		this.mainEventListeners.remove(listener)
	}
	//#endregion
}

interface MainActivityEvent {
	/** Activity main onBackPressed event, return False to prevent activity from handling the event */
	fun onBackPressTriggered(): Boolean
}