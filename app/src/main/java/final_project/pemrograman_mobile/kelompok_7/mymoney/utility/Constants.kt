package final_project.pemrograman_mobile.kelompok_7.mymoney.utility

class Constants {
    companion object {
        val stringHari: Array<out String> = arrayOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
        val stringBulan: Array<out String> = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")

        fun stringHariPendek(index: Int): String { return stringHari[index].take(3) }
        fun stringBulanPendek(index: Int): String { return stringBulan[index].take(3) }
    }
}