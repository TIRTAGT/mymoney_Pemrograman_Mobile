package final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more.category

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import final_project.pemrograman_mobile.kelompok_7.mymoney.MainActivity
import final_project.pemrograman_mobile.kelompok_7.mymoney.R
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.Category
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.CategoryType
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.MonefyDatabase
import final_project.pemrograman_mobile.kelompok_7.mymoney.database.TransactionType
import final_project.pemrograman_mobile.kelompok_7.mymoney.databinding.FragmentMoreMenuCategoryBinding
import final_project.pemrograman_mobile.kelompok_7.mymoney.fragment.more.MoreFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class CategoryFragment : Fragment(), CategoryItemListAdapter.EventListener, AdapterView.OnItemSelectedListener {
    private lateinit var frontend: FragmentMoreMenuCategoryBinding
    private lateinit var activity: MainActivity
    private lateinit var listAdapter: CategoryItemListAdapter
    private lateinit var context: Context
    private val typeSpinnerArray = arrayOf(CategoryType.INCOME, CategoryType.OUTCOME)
    private lateinit var typeSpinnerAdapter: ArrayAdapter<CategoryType>
    private var selectedTypeID = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = requireActivity() as MainActivity
        listAdapter = CategoryItemListAdapter()
        listAdapter.setOnClickListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        frontend = FragmentMoreMenuCategoryBinding.inflate(inflater, container, false)
        return frontend.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.frontend.imageView.setOnClickListener { activity.onBackButtonPressed() }
        this.frontend.button1.setOnClickListener { submit() }

        this.frontend.fragmentMoreMenuCategoryRecyclerview.layoutManager = LinearLayoutManager(this.context)
        this.frontend.fragmentMoreMenuCategoryRecyclerview.adapter = listAdapter

        this.frontend.spinner.onItemSelectedListener = this
        typeSpinnerAdapter = ArrayAdapter(this.context, R.layout.fragment_home_add_transaction_category_listitem, this.typeSpinnerArray)
        this.frontend.spinner.adapter = typeSpinnerAdapter
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        activity.setBottomNavVisibility(false)

        MainScope().launch(Dispatchers.IO) { fetchCategories() }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        activity.setBottomNavVisibility(true)

        super.onDestroy()
    }

    private fun submit() {
        val categoryName = this.frontend.editText1.text.toString()

        if (categoryName.isBlank()) {
            Toast.makeText(this.context, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedTypeID == -1) {
            Toast.makeText(this.context, "Kategori transaksi perlu dipilih", Toast.LENGTH_SHORT).show()
            return
        }

        val categoryId = typeSpinnerAdapter.getItem(selectedTypeID) ?: return
        val category = Category(0, categoryName, categoryId)

        MainScope().launch(Dispatchers.IO) {
            val database = MonefyDatabase.require()
            database.categories().insert(category)

            fetchCategories()
        }
    }

    @WorkerThread
    private fun fetchCategories() {
        val database = MonefyDatabase.require()
        val categories = database.categories().selectAll()

        MainScope().launch(Dispatchers.Main) {
            listAdapter.setDataset(ArrayList(categories))
            listAdapter.notifyDataSetChanged()

            frontend.editText1.isEnabled = false
            frontend.editText1.setText("")
            frontend.editText1.isEnabled = true
        }
    }

    @WorkerThread
    private fun delete(id: Int) {
        if (id <= MonefyDatabase.INITIAL_CATEGORIES_SIZE) {
            MainScope().launch(Dispatchers.Main) {
                Toast.makeText(context, "Tidak dapat menghapus kategori default aplikasi", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val database = MonefyDatabase.require()
        database.categories().deleteById(id)

        fetchCategories()
    }

    override fun onCategoryItemDeleteIconClicked(id: Int) {
        MainScope().launch(Dispatchers.IO) { delete(id) }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedTypeID = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        selectedTypeID = -1
    }
}