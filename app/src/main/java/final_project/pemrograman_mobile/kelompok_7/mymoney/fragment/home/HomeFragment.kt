package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.commit
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivityEvent
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentHomeBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.bulanan.BulananFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.harian.HarianFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.memo.AddMemo
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.memo.MemoFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.home.tutupbuku.TutupBukuFragment
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.Constants
import final_project.pemrograman_mobile.kelompok_7.mymoney.utility.UtilityFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.MessageFormat
import java.time.ZonedDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class HomeFragment : Fragment(), MainActivityEvent {
	private lateinit var frontend: FragmentHomeBinding
	private lateinit var parentActivity: MainActivity
	private lateinit var database: MonefyDatabase
	private val fragmentStack: ArrayList<String> = ArrayList()
	private var currentlyActiveMenu: String? = null
	private val menuEventListeners: ArrayList<HomeFragmentEvent> = ArrayList()
	private var timePagination: ZonedDateTime = ZonedDateTime.now()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		parentActivity = requireActivity() as MainActivity
		database = MonefyDatabase.require()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		frontend = FragmentHomeBinding.inflate(inflater, container, false)
		return frontend.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		this.frontend.fabAdd.setOnClickListener {
			setBottomNavVisibility(false)

			if (this.currentlyActiveMenu == MemoFragment::class.qualifiedName) {
				switchFragment(AddMemo::class, 1)
				return@setOnClickListener
			}

			switchFragment(AddTransaction::class, 1)
		}

		this.frontend.fragmentHomePaginationPrevious.setOnClickListener {
			if (currentlyActiveMenu == BulananFragment::class.qualifiedName) {
				this.timePagination = this.timePagination.minusYears(1)
			}
			else {
				this.timePagination = this.timePagination.minusMonths(1)
			}

			this.RefreshPaginationDisplay()
		}
		this.frontend.fragmentHomePaginationDisplay.setOnLongClickListener {
			this.timePagination = ZonedDateTime.now()
			this.RefreshPaginationDisplay()

			return@setOnLongClickListener true
		}
		this.frontend.fragmentHomePaginationNext.setOnClickListener{
			if (currentlyActiveMenu == BulananFragment::class.qualifiedName) {
				this.timePagination = this.timePagination.plusYears(1)
			}
			else {
				this.timePagination = this.timePagination.plusMonths(1)
			}

			this.RefreshPaginationDisplay()
		}

		this.frontend.fragmentHomeHeaderHarianMenu.setOnClickListener { switchFragment(HarianFragment::class, 0) }
		this.frontend.fragmentHomeHeaderKalenderMenu.setOnClickListener {

		}
		this.frontend.fragmentHomeHeaderBulananMenu.setOnClickListener { switchFragment(BulananFragment::class, 0) }
		this.frontend.fragmentHomeHeaderTutupBukuMenu.setOnClickListener { switchFragment(TutupBukuFragment::class, 0) }
		this.frontend.fragmentHomeHeaderMemoMenu.setOnClickListener { switchFragment(MemoFragment::class, 0) }

		if (this.currentlyActiveMenu == MemoFragment::class.qualifiedName) {
			switchFragment(MemoFragment::class, 0)
			return
		}
		switchFragment(HarianFragment::class, 0)
	}

	override fun onStart() {
		super.onStart()
	}

	override fun onResume() {
		super.onResume()
		parentActivity.addMainEventListener(this)

		this.RefreshPaginationDisplay()
	}

	override fun onPause() {
		super.onPause()
	}

	override fun onBackPressTriggered(): Boolean {
		this.fragmentStack.removeLast()

		if (childFragmentManager.backStackEntryCount == 1) {
			return true
		}

		childFragmentManager.popBackStackImmediate()
		this.currentlyActiveMenu = this.fragmentStack.last()
		applyColorTopNavbar()
		setTopBarVisibility(true)
		setFabVisibility(true)
		return false
	}

	override fun onStop() {
		parentActivity.removeMainEventListener(this)
		super.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
	}

	@MainThread
	fun pressBackButton() {
		this.parentActivity.onBackButtonPressed()
	}

	@MainThread
	fun setFabVisibility(visible: Boolean) {
		this.frontend.fabAdd.visibility = if (visible) View.VISIBLE else View.GONE
	}

	//#region Switch Fragment
	@MainThread
	fun switchFragment(a: Fragment, level: Int = 0) {
		switchFragment(a, level, null)
	}

	@MainThread
	public fun switchFragment(a: KClass<out Fragment>, level: Int = 0) {
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
		if (targetFragmentName == previousFragment) {
			applyColorTopNavbar()
			return
		}

		// Reset color for the bottom navbar
		when (this.currentlyActiveMenu) {
			HarianFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderHarianMenuActiveIcon.visibility = View.GONE
			}
			BulananFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderBulananMenuActiveIcon.visibility = View.GONE
			}
			TutupBukuFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderTutupBukuMenuActiveIcon.visibility = View.GONE
			}
			MemoFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderMemoMenuActiveIcon.visibility = View.GONE
			}
		}

		this.currentlyActiveMenu = targetFragmentName

		if (previousFragment != null) {
			childFragmentManager.popBackStackImmediate()
			this.fragmentStack[level] = targetFragmentName
		}
		else {
			this.fragmentStack.add(targetFragmentName)
		}

		applyColorTopNavbar()

		targetFragment.arguments = data

		childFragmentManager.commit {
			addToBackStack(targetFragmentName)

			replace(R.id.HomeFragmentContainer, targetFragment)
		}

		RefreshPaginationDisplay(false)
	}
	//#endregion

	@MainThread
	fun updateSummaryAmount(income: Double, outcome: Double) {
		this.frontend.fragmentHomeSummaryIncomeText.text = UtilityFunctions.doubleToString(income)
		this.frontend.fragmentHomeSummaryOutcomeText.text = UtilityFunctions.doubleToString(outcome)

		val total = income - outcome
		this.frontend.fragmentHomeSummaryTotalText.text = UtilityFunctions.doubleToString(total)
	}

	@MainThread
	fun setSummaryAmountVisibility(visible: Boolean) {
		this.frontend.linearLayout3.visibility = if (visible) View.VISIBLE else View.GONE
		this.frontend.linearLayout4.visibility = if (visible) View.VISIBLE else View.GONE
	}

	@MainThread
	fun setTopBarVisibility(visible: Boolean) {
		this.frontend.linearLayout.visibility = if (visible) View.VISIBLE else View.GONE
		this.frontend.horizontalScrollView.visibility = if (visible) View.VISIBLE else View.GONE
		this.frontend.linearLayout2.visibility = if (visible) View.VISIBLE else View.GONE
	}

	@MainThread
	fun setBottomNavVisibility(visibility: Boolean) {
		parentActivity.setBottomNavVisibility(visibility)
	}

	fun RefreshPaginationDisplay(broadcast: Boolean = true) {
		val a = UtilityFunctions.deepCopyZonedDateTime(this.timePagination)

		val text: String

		if (currentlyActiveMenu == BulananFragment::class.qualifiedName) {
			text = MessageFormat.format("{0}", *arrayOf(
				a.year.toString()
			))
		}
		else {
			text = MessageFormat.format("{0} {1}", *arrayOf(
				Constants.stringBulanPendek(a.month.ordinal),
				a.year.toString()
			))
		}

		this.frontend.fragmentHomePaginationDisplay.text = text

		if (broadcast) {
			val listeners = ArrayList(menuEventListeners)

			MainScope().launch(Dispatchers.IO) {
				for (listener in listeners) {
					listener.onDatePaginationChanged(a)
				}
			}
		}
	}

	fun addCustomEventListener(listener: HomeFragmentEvent) {
		this.menuEventListeners.add(listener)
	}

	fun removeCustomEventListener(listener: HomeFragmentEvent) {
		this.menuEventListeners.remove(listener)
	}

	fun applyColorTopNavbar() {
		when (this.currentlyActiveMenu) {
			HarianFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderHarianMenuActiveIcon.visibility = View.VISIBLE
			}
			BulananFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderBulananMenuActiveIcon.visibility = View.VISIBLE
			}
			TutupBukuFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderTutupBukuMenuActiveIcon.visibility = View.VISIBLE
			}
			MemoFragment::class.qualifiedName -> {
				frontend.fragmentHomeHeaderMemoMenuActiveIcon.visibility = View.VISIBLE
			}
		}
	}

	fun getCurrentPagination(): ZonedDateTime {
		return UtilityFunctions.deepCopyZonedDateTime(timePagination)
	}
}

abstract interface HomeFragmentEvent {
	@WorkerThread fun onDatePaginationChanged(newDate: ZonedDateTime) {}
}